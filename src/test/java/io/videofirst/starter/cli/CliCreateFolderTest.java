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
package io.videofirst.starter.cli;

import static org.assertj.core.api.Assertions.assertThat;

import io.videofirst.starter.Application;
import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * CLI Integration test to test creating a folder.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public class CliCreateFolderTest extends AbstractCliTest {

    private static final File TEST_FOLDER = new File("test-folder");

    @BeforeEach
    public void setup() {
        super.setup();
        deleteDirectory(TEST_FOLDER);
    }

    @AfterEach
    public void tearDown() {
        deleteDirectory(TEST_FOLDER);
    }

    @Test
    void should_create_folder_using_project_and_folder() {
        Application.main(new String[]{PROJECT_NAME_TEST, TEST_FOLDER.getName()});

        String output = getOutput();
        assertThat(output).contains("Outputting starter for project [ test-project ] to folder [ test-folder ]");
        assertThat(output).contains("Parameters are: {}");

        assertFolderFiles(TEST_FOLDER, JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS);
    }

    @Test
    void should_create_folder_using_project_and_folder_and_all_params() {
        Application.main(new String[]{PROJECT_NAME_ACME, TEST_FOLDER.getName(),
            "package=" + PROJECT_PACKAGE_ACME,
            "description=" + PROJECT_DESCRIPTION_ACME});

        String output = getOutput();
        assertThat(output).contains("Outputting starter for project [ acme-project ] to folder [ test-folder ]");
        assertThat(output).contains("Parameters are: {package=com.acme.corporation, " +
            "description=VFA project for main Acme Corp UI}");

        assertFolderFiles(TEST_FOLDER, JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_NO_PREFIX);
    }

    // Private methods

    private void assertFolderFiles(File testFolder, String expectedJsonResource) {
        assertThat(testFolder.listFiles().length).isPositive();

        // Check alignment of files
        Set<String> actualFilenames = FileUtils.listFiles(testFolder, null, true).stream()
            .map(f -> f.getPath().replaceAll("\\\\", "/")) // windows fix
            .map(f -> f.substring(testFolder.getName().length() + 1)) // remove test folder from start of files
            .collect(Collectors.toSet());

        assertFilesAndFileContents(testFolder, expectedJsonResource, actualFilenames);
    }

}
