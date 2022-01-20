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

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract integration test class containing useful methods which implementing test classes can use.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public abstract class AbstractStarterIntegrationTest {//} implements TestPropertyProvider {

    protected static final String PROJECT_NAME_TEST_PROJECT = "test-project";

    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Inject
    private EmbeddedServer server;

    @BeforeEach
    void setup() throws IOException {
        if (RestAssured.port == server.getPort()) {
            return; // already set up
        }

        // Ensure server is OK by hitting `/health` endpoint.
        try (HttpClient client = server.getApplicationContext()
            .createBean(HttpClient.class, server.getURL())) {
            assertEquals(HttpStatus.OK, client.toBlocking().exchange("/health").status());
        }

        // Set RestAssured port to Micronaut port
        RestAssured.port = server.getPort();
    }

//    @Override
//    public Map<String, String> getProperties() {
//        return null;
//    }

    // Protected methods

    protected String getResource(String resource) throws IOException {
        return IOUtils.toString(AbstractStarterIntegrationTest.class.getResource(resource), Charset.defaultCharset());
    }

    protected Map<String, String> getPreviewResourceAsMap(String resource) throws IOException {
        String expectedPreview = getResource("/json/test-project-default-params.json");
        Map<String, Object> map = OBJECT_MAPPER.readValue(expectedPreview, HashMap.class);
        return (Map<String, String>) map.get("files");
    }

    protected List<String> getFilenamesWithPrefix(Set<String> keySet, String prefix) {
        return keySet.stream().map(f -> prefix + "/" + f).collect(Collectors.toList());
    }

}
