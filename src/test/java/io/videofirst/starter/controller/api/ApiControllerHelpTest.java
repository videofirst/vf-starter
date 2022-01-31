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
 * Integration test to test the API help endpoint i.e. / (GET with header 'Accept=text/plain').
 *
 * @author Bob Marks
 * @since 2022.1
 */
class ApiControllerHelpTest extends AbstractControllerTest {

    @Inject
    public ApiControllerHelpTest(EmbeddedServer server) {
        super(server);
    }

    @Test
    void should_show_help_screen() throws IOException {
        String actualHelpText = given()
            .when()
            .get("/")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.TEXT)
            .extract().asString();

        String expectedHelpText = getResource("/text/help-screen.txt");
        assertThat(actualHelpText).isEqualTo(expectedHelpText);
    }

}
