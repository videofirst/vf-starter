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
package io.videofirst.starter.controller.ui;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.http.ContentType;
import io.videofirst.starter.controller.AbstractControllerTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Integration test to test the API documentation endpoints i.e. /yaml/swagger-api.yml (GET) + /api-docs (GET).
 *
 * @author Bob Marks
 * @since 2022.1
 */
class ApiDocsControllerTest extends AbstractControllerTest {

    @Inject
    public ApiDocsControllerTest(EmbeddedServer server) {
        super(server);
    }

    @Test
    void should_load_swagger_yaml() throws IOException {
        String actualBody = given()
            .when()
            .get("/swagger/api.yml")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.TEXT)
            .extract().asString();

        String expectedBody = getResource("/yaml/swagger-api.yml");
        assertThat(actualBody).isEqualToIgnoringNewLines(expectedBody);
    }

    @Test
    void should_display_api_docs_ui_screen() {
        String actualHtml = given()
            .when()
            .accept(ContentType.HTML)  // mimic browser by setting header `Accept: text/html`
            .get("/api-docs")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .extract().asString();

        assertThat(actualHtml).contains("<html lang='en'>");
        assertThat(actualHtml).contains("<title>Video First Starter - API Docs</title>");
        assertThat(actualHtml).contains("<div id='swagger-ui'></div>");
    }

}
