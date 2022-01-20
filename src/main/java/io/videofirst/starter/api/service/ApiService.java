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
package io.videofirst.starter.api.service;

import static io.videofirst.starter.service.VfStarterFileService.STARTER;
import static org.apache.commons.compress.archivers.zip.UnixStat.FILE_FLAG;

import io.videofirst.starter.exceptions.VfStarterException;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.model.VfStarterFile;
import io.videofirst.starter.model.VfStarterPreview;
import io.videofirst.starter.service.VfStarterFileService;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

/**
 * High level API service class to be called by API controller.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Singleton
@RequiredArgsConstructor
public class ApiService {

    private final VfStarterFileService vfStarterFileService;

    public VfStarterPreview getPreview(Map<String, String> paramValues) {
        Map<String, String> files = new HashMap<>();
        VfStarter starter = vfStarterFileService.getStarters().get(STARTER);
        vfStarterFileService.validateParameters(paramValues, starter);

        if (starter.getFiles() != null) {
            for (VfStarterFile projectFile : starter.getFiles().values()) {
                // Filename is always templated
                String filename = vfStarterFileService.getFilename(projectFile, paramValues);

                String fileContents;
                if (projectFile.getBase64() != null) {  // If binary, just show the size
                    fileContents = "Binary (" + projectFile.getBinary().length + " bytes)";
                } else {
                    fileContents = vfStarterFileService.getFileContents(paramValues, projectFile);
                }

                files.put(filename, fileContents);
            }
        }
        return VfStarterPreview.builder().files(files).build();
    }

    public void createZip(String project, Map<String, String> paramValues, boolean hasPrefix,
        OutputStream outputStream) {
        try {
            ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(outputStream);
            VfStarter starter = vfStarterFileService.getStarters().get(STARTER);
            vfStarterFileService.validateParameters(paramValues, starter);

            String prefix = hasPrefix ? project : "";
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
                    } else if (projectFile.getBinary() != null) {
                        zipOutputStream.write(projectFile.getBinary());
                    } else {
                        throw new VfStarterException("No data available for project file [ " + filename + " ]");
                    }
                    zipOutputStream.closeArchiveEntry();
                }
            }
            zipOutputStream.finish();
            outputStream.flush();
        } catch (IOException ioEx) {
            throw new VfStarterException("IO exception when creating ZIP file");
        }
    }

    public String getHelp() {
        return vfStarterFileService.renderTemplate("help");
    }

}
