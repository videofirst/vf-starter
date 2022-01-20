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
package io.videofirst.starter.ui;

import static io.micronaut.core.util.CollectionUtils.mapOf;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.views.View;
import io.swagger.v3.oas.annotations.Hidden;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.ui.service.UiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller which serves main UI (user interface).
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Slf4j
@Controller("/ui")
@RequiredArgsConstructor
@Hidden
class UiController {

    private final UiService service;

    @Get
    @View("/ui/index")
    @Produces(MediaType.TEXT_HTML)
    public HttpResponse index() {
        return HttpResponse.ok(getModel());
    }

    // Private methods

    private Object getModel() {
        // Currently, just one starter (VFA) - will support multiple in the future.
        VfStarter starter = service.getVfStarter();
        return mapOf("starter", starter);
    }

}