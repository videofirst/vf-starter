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
import io.videofirst.starter.controller.AbstractControllerTest;
import io.videofirst.starter.exceptions.VfStarterException;
import jakarta.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
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
class ApiControllerZipTest extends AbstractControllerTest {

    private static String CONTENT_TYPE_APPLICATION_ZIP = "application/zip";

    private static String TEST_PROJECT_ZIP_FILENAME = PROJECT_NAME_TEST + ".zip";
    private static File TEST_PROJECT_ZIP_FILE = new File(TEST_PROJECT_ZIP_FILENAME);

    private static String ACME_PROJECT_ZIP_FILENAME = PROJECT_NAME_ACME + ".zip";
    private static File ACME_PROJECT_ZIP_FILE = new File(ACME_PROJECT_ZIP_FILENAME);

    private static File UNZIP_FOLDER = new File("test-unzip-folder");

    @Inject
    public ApiControllerZipTest(EmbeddedServer server) {
        super(server);
    }

    @BeforeEach
    public void setup() {
        super.setup();
        forceDelete(TEST_PROJECT_ZIP_FILE);
        forceDelete(ACME_PROJECT_ZIP_FILE);

        try {
            FileUtils.forceMkdir(UNZIP_FOLDER);
            FileUtils.cleanDirectory(UNZIP_FOLDER);
        } catch (IOException e) {
            throw new VfStarterException(e);
        }

        checkZipFilesDeletedAndUnzipFolderAreEmpty();
    }

    @AfterEach
    void tearDown() throws IOException {
        forceDelete(TEST_PROJECT_ZIP_FILE);
        forceDelete(ACME_PROJECT_ZIP_FILE);
        FileUtils.deleteDirectory(UNZIP_FOLDER);
    }

    @Test
    void should_zip_with_default_params() throws IOException {
        try (InputStream is = given()
            .when()
            .get("/" + PROJECT_NAME_TEST + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            copy(is, TEST_PROJECT_ZIP_FILE);
            Set<String> unzippedFiles = unzip(TEST_PROJECT_ZIP_FILE, UNZIP_FOLDER);
            assertFilesAndFileContents(UNZIP_FOLDER, JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS_WITH_PREFIX,
                unzippedFiles);
        }
    }

    @Test
    void should_zip_with_default_params_and_prefix() throws IOException {
        try (InputStream is = given()
            .when()
            .param("prefix", "true")
            .get("/" + PROJECT_NAME_TEST + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            copy(is, TEST_PROJECT_ZIP_FILE);
            Set<String> unzippedFiles = unzip(TEST_PROJECT_ZIP_FILE, UNZIP_FOLDER);
            assertFilesAndFileContents(UNZIP_FOLDER, JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS_WITH_PREFIX,
                unzippedFiles);
        }
    }

    @Test
    void should_zip_with_default_params_and_no_prefix() throws IOException {
        try (InputStream is = given()
            .when()
            .param("prefix", "false")
            .get("/" + PROJECT_NAME_TEST + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            copy(is, TEST_PROJECT_ZIP_FILE);
            Set<String> unzippedFiles = unzip(TEST_PROJECT_ZIP_FILE, UNZIP_FOLDER);
            assertFilesAndFileContents(UNZIP_FOLDER, JSON_PREVIEW_TEST_PROJECT_DEFAULT_PARAMS,
                unzippedFiles);
        }
    }

    @Test
    void should_zip_with_all_params_and_prefix() throws IOException {
        try (InputStream is = given()
            .when()
            .param("package", PROJECT_PACKAGE_ACME)
            .param("description", PROJECT_DESCRIPTION_ACME)
            .param("prefix", "true")
            .get("/" + PROJECT_NAME_ACME + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            copy(is, ACME_PROJECT_ZIP_FILE);
            Set<String> unzippedFiles = unzip(ACME_PROJECT_ZIP_FILE, UNZIP_FOLDER);
            assertFilesAndFileContents(UNZIP_FOLDER, JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_WITH_PREFIX,
                unzippedFiles);
        }
    }

    @Test
    void should_zip_with_all_params_and_no_prefix() throws IOException {
        try (InputStream is = given()
            .when()
            .param("package", PROJECT_PACKAGE_ACME)
            .param("description", PROJECT_DESCRIPTION_ACME)
            .param("prefix", "false")
            .get("/" + PROJECT_NAME_ACME + ".zip")
            .then()
            .log().headers()
            .statusCode(HttpStatus.CREATED.getCode())
            .contentType(CONTENT_TYPE_APPLICATION_ZIP)
            .extract().asInputStream()) {

            copy(is, ACME_PROJECT_ZIP_FILE);
            Set<String> unzippedFiles = unzip(ACME_PROJECT_ZIP_FILE, UNZIP_FOLDER);
            assertFilesAndFileContents(UNZIP_FOLDER, JSON_PREVIEW_ACME_PROJECT_ALL_PARAMS_NO_PREFIX,
                unzippedFiles);
        }
    }

    // Private methods

    /**
     * Wrapper for IOUtils.copy (InputStream, FileOutputStream) using a try-with-resources statement so the
     * FileOutputStream closes correctly afterwards.
     */
    private void copy(InputStream is, File file) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            IOUtils.copy(is, fos);
        }
    }

    /**
     * Force delete wrapper (which throws a VfStarterException) if there are any problems.
     */
    private void forceDelete(File file) {
        try {
            if (file.exists()) {
                FileUtils.forceDelete(file);
            }
        } catch (IOException ioEx) {
            throw new VfStarterException(ioEx);
        }
    }

    /**
     * Ensure ZIP files and output are empty before proceeding.
     */
    private void checkZipFilesDeletedAndUnzipFolderAreEmpty() {
        assertThat(TEST_PROJECT_ZIP_FILE).doesNotExist();
        assertThat(ACME_PROJECT_ZIP_FILE).doesNotExist();
        assertThat(UNZIP_FOLDER.listFiles().length).isZero();
    }

    /**
     * Unzip a file to an unzip folder.
     */
    private Set<String> unzip(File unzipFile, File unzipFolder) throws IOException {
        Set<String> files = new HashSet<>();

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
