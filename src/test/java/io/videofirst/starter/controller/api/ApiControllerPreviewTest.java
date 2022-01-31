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

import io.micronaut.http.HttpStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.http.ContentType;
import io.videofirst.starter.controller.AbstractControllerTest;
import jakarta.inject.Inject;
import java.io.IOException;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

/**
 * Integration test to test the API preview endpoint i.e. /{project.json} (GET).
 *
 * @author Bob Marks
 * @since 2022.1
 */
class ApiControllerPreviewTest extends AbstractControllerTest {

    @Inject
    public ApiControllerPreviewTest(EmbeddedServer server) {
        super(server);
    }

    @Test
    void should_preview_with_default_params() throws IOException, JSONException {
        String actualJson = given()
            .when()
            .get("/" + PROJECT_NAME_TEST + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource(JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS_WITH_PREFIX); // default is prefix
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

    @Test
    void should_preview_with_default_params_and_prefix() throws JSONException {
        String actualJson = given()
            .when()
            .param("prefix", "true")
            .get("/" + PROJECT_NAME_TEST + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource(JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS_WITH_PREFIX);
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

    @Test
    void should_preview_with_default_params_and_no_prefix() throws JSONException {
        String actualJson = given()
            .when()
            .param("prefix", "false")
            .get("/" + PROJECT_NAME_TEST + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource(JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS);
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

    @Test
    void should_preview_with_all_params_with_prefix() throws JSONException {
        String actualJson = given()
            .when()
            .param("package", PROJECT_PACKAGE_ACME)
            .param("description", PROJECT_DESCRIPTION_ACME)
            .param("prefix", "true")
            .get("/" + PROJECT_NAME_ACME + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource(JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_WITH_PREFIX);
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

    @Test
    void should_preview_with_all_params_and_no_prefix() throws JSONException {
        String actualJson = given()
            .when()
            .param("package", PROJECT_PACKAGE_ACME)
            .param("description", PROJECT_DESCRIPTION_ACME)
            .param("prefix", "false")
            .get("/" + PROJECT_NAME_ACME + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource(JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_NO_PREFIX);
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

}
