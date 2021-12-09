package io.videofirst.starter.service;

import static io.videofirst.starter.service.VfStarterService.STARTER;
import static org.apache.commons.compress.archivers.zip.UnixStat.FILE_FLAG;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.videofirst.starter.exceptions.VfStarterException;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.model.VfStarterFile;
import io.videofirst.starter.model.VfStarterParams;
import io.videofirst.starter.model.VfStarterPreview;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

@Singleton
@RequiredArgsConstructor
public class VfApiService {

    private final VfStarterService starterService;

    public VfStarterPreview getPreview(VfStarterParams params) {
        Map<String, String> files = new HashMap<>();

        VfStarter starter = starterService.getStarters().get(STARTER);
        if (starter.getFiles() != null) {
            for (VfStarterFile projectFile : starter.getFiles().values()) {
                // Filename is always templated
                String filename = getFilename(projectFile, params); // params.getProject() + "/"

                String fileContents;
                if (projectFile.getBase64() != null) {  // If binary, just show the size
                    fileContents = "Binary (" + projectFile.getBinary().length + " bytes)";
                } else {
                    fileContents = getFileContents(params, projectFile);
                }

                files.put(filename, fileContents);
            }
        }
        return VfStarterPreview.builder().files(files).build();
    }

    private String getFileContents(VfStarterParams params, VfStarterFile projectFile) {
        // File contents can be (1) Template (2) Plain or (3) Binary
        if (projectFile.getTemplateContents() != null) {
            return renderTemplate(projectFile.getTemplateContents(), params);
        } else if (projectFile.getRaw() != null) {
            return projectFile.getRaw();
        }
        return null;
    }

    public void createZip(VfStarterParams params, OutputStream outputStream) {
        try {
            ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(outputStream);
            VfStarter starter = starterService.getStarters().get(STARTER);
            if (starter.getFiles() != null) {
                for (VfStarterFile projectFile : starter.getFiles().values()) {
                    // Filename is always templated
                    String filename = params.getProject() + "/" +
                        renderTemplate(projectFile.getTemplateFilename(), params);

                    // File contents can be (1) Template (2) Plain or (3) Binary
                    String fileContents = null;  // show nothing for
                    if (projectFile.getTemplateContents() != null) {
                        fileContents = renderTemplate(projectFile.getTemplateContents(), params);
                    } else if (projectFile.getRaw() != null) {
                        fileContents = projectFile.getRaw();
                    }

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

    // Private methods

    private String getFilename(VfStarterFile projectFile, VfStarterParams params) {
        return getFilename(projectFile, params, null);
    }

    private String getFilename(VfStarterFile projectFile, VfStarterParams params, String prefix) {
        String filename = null;
        prefix = prefix != null ? prefix : "";
        if (projectFile.getTemplateFilename() != null) {
            return prefix + renderTemplate(projectFile.getTemplateFilename(), params);
        } else {
            return projectFile.getId();  // the id is the filename
        }
    }

    private String renderTemplate(Template template, VfStarterParams params) {
        if (template == null) {
            return null;
        }
        try {
            Map<String, Object> model = new HashMap<>();
            model.put("params", params);
            Writer out = new StringWriter();
            template.process(model, out);
            return out.toString();
        } catch (TemplateException | IOException e) {
            throw new VfStarterException("Issue rendering template", e);
        }
    }

}
