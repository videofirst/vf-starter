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
import static org.assertj.core.api.Assertions.assertThat;

import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration test to test the API project download endpoint i.e. /{project}.zip (GET).
 *
 * @author Bob Marks
 * @since 2022.1
 */
@MicronautTest
class ApiControllerZipTest extends AbstractStarterIntegrationTest {

    private static String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";
    private static String ZIP_FILENAME = "test-project.zip";

    private static File ZIP_FILE = new File(ZIP_FILENAME);
    private static File UNZIP_FOLDER = new File("test-unzip-folder");

    @BeforeEach
    void setup() throws IOException {
        super.setup();
        this.ZIP_FILE.delete();

        FileUtils.forceMkdir(UNZIP_FOLDER);
        FileUtils.cleanDirectory(UNZIP_FOLDER);

        checkZipFileAndUnzipFolderAreEmpty();
    }

    @AfterEach
    void tearDown() throws IOException {
        this.ZIP_FILE.delete();
        FileUtils.deleteDirectory(UNZIP_FOLDER);
    }

    @Test
    void should_zip_with_default_params() throws IOException {
        String prefix = PROJECT_NAME_TEST_PROJECT;
        try (InputStream is = given()
            .when()
            .get("/" + PROJECT_NAME_TEST_PROJECT + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            IOUtils.copy(is, new FileOutputStream(ZIP_FILE));
            List<String> unzippedFiles = unzip(ZIP_FILE, UNZIP_FOLDER);

            assertUnzippedFile(UNZIP_FOLDER, unzippedFiles, "/json/test-project-default-params.json", prefix);
        }
    }

    // Private methods

    private void assertUnzippedFile(File unzipFolder, List<String> unzippedFiles, String expectedJsonResource,
        String prefix)
        throws IOException {
        Map<String, String> expectedFiles = getPreviewResourceAsMap(expectedJsonResource);

        // First, check filenames are completely aligned
        List<String> prefixedFiles = getFilenamesWithPrefix(expectedFiles.keySet(), prefix);
        assertThat(unzippedFiles).containsExactlyInAnyOrderElementsOf(prefixedFiles);

        // Second, compare contents of each file
        File projectFolder = new File(unzipFolder, prefix);
        for (String filename : expectedFiles.keySet()) {
            // Expected
            String expectedFileContent = expectedFiles.get(filename);

            // Actual
            File file = new File(projectFolder, filename);
            if (expectedFileContent.startsWith("Binary (")) {
                long actualFileSize = file.length();
                long expectedFileSize = Long.parseLong(expectedFileContent.replaceAll("\\D+", ""));
                assertThat(actualFileSize).isEqualTo(expectedFileSize);
            } else {
                String actualFileContent = FileUtils.readFileToString(file, Charset.defaultCharset());
                assertThat(actualFileContent)
                    .withFailMessage("Misaligned content for file: " + filename) // helps debug
                    .isEqualTo(expectedFileContent);
            }
        }
    }

    private void checkZipFileAndUnzipFolderAreEmpty() {
        assertThat(ZIP_FILE).doesNotExist();
        assertThat(UNZIP_FOLDER.listFiles().length).isZero();
    }

    private List<String> unzip(File unzipFile, File unzipFolder) throws IOException {
        List<String> files = new ArrayList<>();

        Path destFolderPath = unzipFolder.toPath();
        try (ZipFile zipFile = new ZipFile(unzipFile, ZipFile.OPEN_READ, Charset.defaultCharset())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = destFolderPath.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        try (OutputStream out = new FileOutputStream(entryPath.toFile())) {
                            files.add(destFolderPath.relativize(entryPath).toString().replaceAll("\\\\", "/"));
                            IOUtils.copy(in, out);
                        }
                    }
                }
            }
        }
        return files;
    }

}
