/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.videofirst.starter.controller.api;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.annotation.QueryValue;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.videofirst.starter.configuration.properties.VfStarterConfigProps;
import io.videofirst.starter.controller.api.service.ApiService;
import io.videofirst.starter.model.VfStarterPreview;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Map;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Main controller for the VF Starter API endpoints.
 *
 * Supports endpoints to show API help screen (plain text), project preview (JSON) and download project (ZIP).
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Slf4j
@Controller
public class ApiController {

    public static final String MEDIA_TYPE_APPLICATION_ZIP = "application/zip";

    private final ApiService apiService;
    private final VfStarterConfigProps config;

    public ApiController(ApiService apiService, VfStarterConfigProps config) {
        this.apiService = apiService;
        this.config = config;
    }

    /**
     * Returns help as plain-text.
     */
    @Get
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    @ApiResponse(
        responseCode = "200",
        description = "Plain text description of the API",
        content = @Content(mediaType = MediaType.TEXT_PLAIN)
    )
    public HttpResponse<String> home(HttpRequest<?> request) {
        Collection<MediaType> accept = request.accept();
        if (accept.contains(MediaType.TEXT_HTML_TYPE)) {
            return HttpResponse.permanentRedirect(config.getUiPath());
        } else {
            String help = apiService.getHelp();
            return HttpResponse.ok(help);
        }
    }

    /**
     * Returns a JSON preview of project.
     *
     * @param project Name of your project
     * @param parameters Map of parameters e.g. package, description
     */
    @Get("{project}.json{?values*}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(
        responseCode = "200",
        description = "JSON based preview of a new project",
        content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public VfStarterPreview preview(@PathVariable("project") String project,
        @Nullable @QueryValue("values") Map<String, String> parameters,
        @QueryValue(value = "prefix", defaultValue = "true") boolean prefix) {
        return apiService.getPreview(project, parameters, prefix);
    }

    /**
     * Generate project ZIP file.
     *
     * @param project Name of your project
     * @param prefix Boolean, if true the ZIP file will have a root folder (project).
     * @param parameters Map of parameters e.g. package, description
     */
    @Get("/{project}.zip{?values*}")
    @Produces(MEDIA_TYPE_APPLICATION_ZIP)
    @ApiResponse(
        description = "ZIP file containing generated project",
        content = @Content(
            mediaType = MEDIA_TYPE_APPLICATION_ZIP
        )
    )
    public HttpResponse<Writable> generateZip(@PathVariable("project") String project,
        @NotNull @QueryValue("values") Map<String, String> parameters,
        @QueryValue(value = "prefix", defaultValue = "true") boolean prefix) {
        MutableHttpResponse<Writable> response = HttpResponse.created(new Writable() {
            @Override
            public void writeTo(OutputStream outputStream, @Nullable Charset charset) throws IOException {
                try {
                    apiService.createZip(project, parameters, prefix, outputStream);
                    outputStream.flush();
                } catch (Exception e) {
                    log.error("Error generating ZIP: " + e.getMessage(), e);
                    throw new IOException(e.getMessage(), e);
                }
            }

            @Override
            public void writeTo(Writer out) {
                // No need to implement
            }
        });
        return response.header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=" + project + ".zip");
    }

}
