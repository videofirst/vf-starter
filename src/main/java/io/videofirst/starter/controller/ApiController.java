package io.videofirst.starter.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.io.Writable;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.videofirst.starter.model.VfStarterParams;
import io.videofirst.starter.model.VfStarterPreview;
import io.videofirst.starter.service.VfApiService;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Main controller for the VFA starter API.
 *
 * @author Bob Marks
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class ApiController {

    public static final String MEDIA_TYPE_APPLICATION_ZIP = "application/zip";

    private final VfApiService apiService;

    // TODO put into controller method
    private static final VfStarterParams params = VfStarterParams.builder()
        .project("example")
        .group("com.example.tests")
        .version("2021.1")
        .build();

    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    @ApiResponse(
        responseCode = "200",
        description = "A textual description of the API",
        content = @Content(mediaType = MediaType.TEXT_PLAIN)
    )
    public String home() {
        // TODO add redirect to UI if required
        return "Welcome to VFA Starter";
    }

    @Get("/preview")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiResponse(
        responseCode = "200",
        description = "A preview of a new project",
        content = @Content(mediaType = MediaType.APPLICATION_JSON)
    )
    public VfStarterPreview preview() {
        return apiService.getPreview(params);
    }

    @Get(uri = "/{project}.zip", produces = MEDIA_TYPE_APPLICATION_ZIP)
    @ApiResponse(
        description = "A ZIP file containing the generated application.",
        content = @Content(
            mediaType = MEDIA_TYPE_APPLICATION_ZIP
        )
    )
    public HttpResponse<Writable> createZip() {
        MutableHttpResponse<Writable> response = HttpResponse.created(new Writable() {
            @Override
            public void writeTo(OutputStream outputStream, @Nullable Charset charset) throws IOException {
                try {

                    apiService.createZip(params, outputStream);
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
            "attachment; filename=" + params.getProject() + ".zip");
    }

}
