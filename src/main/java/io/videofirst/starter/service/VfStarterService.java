package io.videofirst.starter.service;

import static freemarker.template.Configuration.VERSION_2_3_31;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.template.Configuration;
import freemarker.template.Template;
import io.micronaut.context.annotation.Context;
import io.videofirst.starter.exceptions.VfStarterException;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.model.VfStarterFile;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;

/**
 * Vfa Starter Service.  This service reads starters from the class path, parses and validates them.
 */
@Context
public class VfStarterService {

    public static final String STARTER = "vfa";  // make this dynamic in future

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
    private Configuration freemarkerConfiguration = new Configuration(VERSION_2_3_31);

    @Getter
    private Map<String, VfStarter> starters = new HashMap<>();

    public VfStarterService() throws IOException {
        freemarkerConfiguration.setClassForTemplateLoading(getClass(), "/freemarker");
        freemarkerConfiguration.setDefaultEncoding("utf-8");
        freemarkerConfiguration.addAutoInclude("/global.ftl");
        freemarkerConfiguration.setNumberFormat("0.######");

        // Set up ObjectMapper and read YAML file
        YAML_MAPPER.findAndRegisterModules();
        String starterFilename = "starters/" + STARTER + ".starter.yml";  // Make dynamic in future
        File starterFile = new File(this.getClass().getClassLoader().getResource(starterFilename).getFile());
        VfStarter starter = YAML_MAPPER.readValue(starterFile, VfStarter.class);

        // Validate and if valid then init templates and insert into map
        validate(starter);
        initTemplates(starter);
        this.starters.put(STARTER, starter);
    }

    private void validate(VfStarter starter) {
        // todo
    }

    private void initTemplates(VfStarter starter) throws IOException {
        for (Entry<String, VfStarterFile> entry : starter.getFiles().entrySet()) {
            String id = entry.getKey();
            VfStarterFile templateItem = entry.getValue();

            // 1) Filename
            templateItem.setId(id);
            if (templateItem.getFilename() != null) {  // template
                Template templateFilename = new Template("filename-" + id,
                    new StringReader(templateItem.getFilename().trim()), freemarkerConfiguration);
                templateItem.setTemplateFilename(templateFilename);
            }

            // 2) File contents
            String template = templateItem.getFile();
            if (template != null) {
                Template templateContents = new Template("contents-" + id,
                    new StringReader(template.trim()), freemarkerConfiguration);
                templateItem.setTemplateContents(templateContents);
            } else if (templateItem.getRaw() != null) {
                templateItem.setRaw(templateItem.getRaw().trim());
            } else if (templateItem.getBase64() != null) {
                // Parse binary
                String base64 = templateItem.getBase64().trim();
                byte[] binaryBytes = Base64.getDecoder().decode(base64);
                templateItem.setBinary(binaryBytes);
            } else {
                throw new VfStarterException("Please specify either [ template, plain, base64 ] for file [ " +
                    id + " ]");
            }
        }
    }

}