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
package io.videofirst.starter;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.videofirst.starter.exceptions.VfStarterException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Root abstract integration test class which all integration test classes should extend.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@MicronautTest
public abstract class AbstractIntegrationTest {

    protected static final String SSL_TRUST_STORE_NAME = "src/main/resources/vf-starter-self-signed.p12";
    protected static final String SSL_TRUST_STORE_PASSWORD = "changeit";
    protected static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Test project

    protected static final String PROJECT_NAME_TEST = "test-project";
    protected static final String JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS = "/json/test-project-default-params.json";
    protected static final String JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS_WITH_PREFIX =
        "/json/test-project-default-params-with-prefix.json";

    // Acme project

    protected static final String PROJECT_NAME_ACME = "acme-project";
    protected static final String PROJECT_PACKAGE_ACME = "com.acme.corporation";
    protected static final String PROJECT_DESCRIPTION_ACME = "VFA project for main Acme Corp UI";
    protected static final String JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_WITH_PREFIX =
        "/json/acme-project-all-params-with-prefix.json";
    protected static final String JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_NO_PREFIX =
        "/json/acme-project-all-params-no-prefix.json";

    // Protected methods

    protected String readFileToString(File file) {
        try {
            return FileUtils.readFileToString(file, Charset.defaultCharset());
        } catch (IOException ex) {
            throw new VfStarterException(ex);
        }
    }

    protected String getResource(String resource) {
        try {
            return IOUtils.toString(AbstractIntegrationTest.class.getResource(resource), Charset.defaultCharset());
        } catch (IOException ex) {
            throw new VfStarterException(ex);
        }
    }

    protected Map<String, String> getPreviewResourceAsMap(String resource) {
        try {
            String expectedPreview = getResource(resource);
            Map<String, Object> map = OBJECT_MAPPER.readValue(expectedPreview, HashMap.class);
            return (Map<String, String>) map.get("files");
        } catch (IOException ex) {
            throw new VfStarterException(ex);
        }
    }

    protected void assertFilesAndFileContents(File testFolder, String expectedJsonResource,
        Set<String> actualFilenames) {
        Map<String, String> expectedFiles = getPreviewResourceAsMap(expectedJsonResource);
        assertThat(actualFilenames).containsExactlyInAnyOrderElementsOf(expectedFiles.keySet());

        for (String filename : expectedFiles.keySet()) {
            // Expected
            String expectedFileContent = expectedFiles.get(filename);

            // Actual
            File file = new File(testFolder, filename);
            if (expectedFileContent.startsWith("Binary (")) {
                long actualFileSize = file.length();
                long expectedFileSize = Long.parseLong(expectedFileContent.replaceAll("\\D+", ""));
                assertThat(actualFileSize).isEqualTo(expectedFileSize);
            } else {
                String actualFileContent = readFileToString(file);
                assertThat(actualFileContent)
                    .withFailMessage("Misaligned content for file: " + filename) // helps debug
                    .isEqualTo(expectedFileContent);
            }
        }
    }

}
