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
package io.videofirst.starter.api;

import static io.restassured.RestAssured.given;

import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.http.ContentType;
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
@MicronautTest
class ApiControllerPreviewTest extends AbstractStarterIntegrationTest {

    @Test
    void should_preview_with_default_params() throws IOException, JSONException {
        String actualJson = given()
            .when()
            .get("/" + PROJECT_NAME_TEST_PROJECT + ".json")
            .then()
            .log().all()
            .statusCode(HttpStatus.OK.getCode())
            .contentType(ContentType.JSON)
            .extract().asString();

        String expectedPreview = getResource("/json/test-project-default-params.json");
        JSONAssert.assertEquals(expectedPreview, actualJson, false);
    }

}
