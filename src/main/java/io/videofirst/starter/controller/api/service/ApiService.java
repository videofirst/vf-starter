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
package io.videofirst.starter.controller.api.service;

import static io.videofirst.starter.service.VfStarterFileService.STARTER;
import static org.apache.commons.compress.archivers.zip.UnixStat.FILE_FLAG;

import io.videofirst.starter.exceptions.VfStarterException;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.model.VfStarterFile;
import io.videofirst.starter.model.VfStarterPreview;
import io.videofirst.starter.service.VfStarterFileService;
import jakarta.inject.Singleton;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.IOUtils;

/**
 * High level API service class, designed to be invoked by an API controller / CLI.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Slf4j
@Singleton
@RequiredArgsConstructor
public class ApiService {

    private final VfStarterFileService vfStarterFileService;

    public VfStarterPreview getPreview(String project, Map<String, String> paramValues, boolean hasPrefix) {
        VfStarter starter = vfStarterFileService.getStarters().get(STARTER);
        initParameters(starter, project, paramValues);

        String prefix = hasPrefix ? project : "";
        Map<String, String> files = new HashMap<>();
        String selectedFilename = null;
        if (starter.getFiles() != null) {
            for (VfStarterFile starterFile : starter.getFiles().values()) {

                // Filename
                String filename = vfStarterFileService.getFilename(starterFile, paramValues, prefix);
                if (starterFile.isSelected()) {
                    selectedFilename = filename;
                }

                // File contents
                String fileContents;
                if (starterFile.isBinary()) {  // If binary, just show the size
                    fileContents = "Binary (" + starterFile.getBytes().length + " bytes)";
                } else {
                    fileContents = vfStarterFileService.getFileContents(paramValues, starterFile);
                }

                files.put(filename, fileContents);
            }
        }
        // Default selected filename to first filename if not set
        if (selectedFilename == null) {
            selectedFilename = files.keySet().stream().findFirst().get();
        }

        return VfStarterPreview.builder()
            .files(files)
            .selectedFilename(selectedFilename)
            .build();
    }

    public void createZip(String project, Map<String, String> paramValues, boolean hasPrefix,
        OutputStream outputStream) {
        try {
            VfStarter starter = vfStarterFileService.getStarters().get(STARTER);
            initParameters(starter, project, paramValues);

            String prefix = hasPrefix ? project : "";
            ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(outputStream);
            if (starter.getFiles() != null) {
                for (VfStarterFile projectFile : starter.getFiles().values()) {
                    String filename = vfStarterFileService.getFilename(projectFile, paramValues, prefix);

                    // File contents can be (1) Template (2) Plain or (3) Binary
                    String fileContents = vfStarterFileService.getFileContents(paramValues, projectFile);

                    // Create new Zip archive entry
                    ZipArchiveEntry zipEntry = new ZipArchiveEntry(filename);
                    zipEntry.setUnixMode(FILE_FLAG | (projectFile.isExecutable() ? 0775 : 0644));
                    zipEntry.setTime(System.currentTimeMillis());
                    zipOutputStream.putArchiveEntry(zipEntry);

                    // Add data to zip output stream and close archive entry
                    if (fileContents != null) {
                        zipOutputStream.write(fileContents.getBytes(StandardCharsets.UTF_8));
                    } else if (projectFile.getBytes() != null) {
                        zipOutputStream.write(projectFile.getBytes());
                    } else {
                        throw new VfStarterException("No data available for project file [ " + filename + " ]");
                    }
                    zipOutputStream.closeArchiveEntry();
                }
            }
            zipOutputStream.finish();
            outputStream.flush();
        } catch (IOException ioEx) {
            throw new VfStarterException("IO exception when creating ZIP file", ioEx);
        }
    }

    public void createToFolder(String project, String output, Map<String, String> paramValues) {
        File outputFolder = new File(output);
        log.info("Outputting starter for project [ {} ] to folder [ {} ]", project, outputFolder);
        log.info("Parameters are: {}", paramValues);

        VfStarter starter = vfStarterFileService.getStarters().get(STARTER);
        initParameters(starter, project, paramValues);
        try {
            if (starter.getFiles() != null) {
                for (VfStarterFile projectFile : starter.getFiles().values()) {
                    String filename = vfStarterFileService.getFilename(projectFile, paramValues);
                    File file = new File(outputFolder, filename);
                    file.getParentFile().mkdirs();

                    // File contents can be (1) Template (2) Plain or (3) Binary
                    String fileContents = vfStarterFileService.getFileContents(paramValues, projectFile);
                    FileOutputStream fos = new FileOutputStream(file);
                    if (fileContents != null) {
                        IOUtils.write(fileContents.getBytes(StandardCharsets.UTF_8), fos);
                    } else if (projectFile.getBytes() != null) {
                        IOUtils.write(projectFile.getBytes(), fos);
                    } else {
                        throw new VfStarterException("No data available for project file [ " + filename + " ]");
                    }
                    fos.close();
                }
            }
        } catch (IOException ioEx) {
            throw new VfStarterException("IO exception when creating folder", ioEx);
        }
    }

    public String getHelp() {
        return vfStarterFileService.renderTemplate("help");
    }

    // Private methods

    private void initParameters(VfStarter starter, String project, Map<String, String> paramValues) {
        paramValues.put("project", project); // override project in parameters
        // Validate
        vfStarterFileService.validateParameters(paramValues, starter);
    }

}
