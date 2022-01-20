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

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.http.ContentType;
import io.videofirst.starter.api.AbstractStarterIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * Integration test to test the UI endpoint i.e. / (GET with header 'Accept=text/html') and /ui (GET).
 *
 * @author Bob Marks
 * @since 2022.1
 */
@MicronautTest
class UiControllerTest extends AbstractStarterIntegrationTest {

    @Test
    void should_redirect_to_ui_screen_if_loaded_from_browser() {
        given()
            .when()
            .redirects().follow(false)
            .accept(ContentType.HTML)  // mimic browser by setting header `Accept: text/html`
            .get("/")
            .then()
            .log().all()
            .statusCode(HttpStatus.PERMANENT_REDIRECT.getCode())
            .header("Location", "/ui")
            .extract().asString();
    }

    @Test
    void should_display_ui_screen() {
        String actualHtml = given()
            .when()
            .accept(ContentType.HTML)  // mimic browser by setting header `Accept: text/html`
            .get("/ui")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .extract().asString();

        assertThat(actualHtml).contains("<html lang=\"en\">");
        assertThat(actualHtml).contains("<title>Video First - Starter</title>");
    }

}
