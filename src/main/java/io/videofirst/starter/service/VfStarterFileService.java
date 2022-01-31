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
package io.videofirst.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import freemarker.template.Template;
import io.micronaut.context.annotation.Context;
import io.videofirst.starter.exceptions.VfStarterException;
import io.videofirst.starter.exceptions.VfStarterValidationException;
import io.videofirst.starter.model.VfStarter;
import io.videofirst.starter.model.VfStarterFile;
import io.videofirst.starter.model.VfStarterParam;
import io.videofirst.starter.model.VfStarterParamValidation;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import lombok.Getter;

/**
 * This service loads starter files (currently just 'vfa.starter.yml') from the class path, parses, validates and
 * renders out contents (by delegating to TemplateRenderService) when run-time parameters are supplied.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Context
public class VfStarterFileService {

    public static final String STARTER = "vfa";  // make this dynamic in future

    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private final TemplateRenderService templateService;

    @Getter
    private Map<String, VfStarter> starters = new HashMap<>();

    @Inject
    public VfStarterFileService(TemplateRenderService templateService) throws IOException {
        this.templateService = templateService;

        // Set up ObjectMapper and read YAML file
        YAML_MAPPER.findAndRegisterModules();
        loadStarters();
    }

    public void validateParameters(Map<String, String> paramValues, VfStarter starter) {
        for (VfStarterParam starterParam : starter.getParameters().values()) {
            String paramId = starterParam.getId();
            if (!paramValues.containsKey(paramId)) { // If doesn't contain, set default value
                paramValues.put(paramId, starterParam.getValue());
            } else { // Otherwise perform validation
                String paramValue = paramValues.get(paramId);
                validateParam(starterParam, paramValue);
            }
        }
    }

    public String getFilename(VfStarterFile projectFile, Map<String, String> parameters) {
        return getFilename(projectFile, parameters, null);
    }

    public String getFilename(VfStarterFile projectFile, Map<String, String> parameters, String prefix) {
        String filePrefix = prefix != null && !prefix.trim().isEmpty() ? prefix.trim() + "/" : "";
        if (projectFile.getTemplateFilename() != null) {
            Map<String, Object> templateModel = getTemplateModel(parameters);
            return filePrefix + templateService.renderTemplate(projectFile.getTemplateFilename(), templateModel);
        } else {
            return filePrefix + projectFile.getId();  // the id is the filename
        }
    }

    public String getFileContents(Map<String, String> parameters, VfStarterFile projectFile) {
        // File contents can be (1) Template (2) Plain or (3) Binary
        if (projectFile.getTemplateContents() != null) {
            Map<String, Object> templateModel = getTemplateModel(parameters);
            return templateService.renderTemplate(projectFile.getTemplateContents(), templateModel);
        } else if (projectFile.getRaw() != null) {
            return projectFile.getRaw();
        }
        return null;
    }

    public String renderTemplate(String templateFilename) {
        try {
            return templateService.renderTemplate(templateFilename, new HashMap<>());
        } catch (Exception e) {
            throw new VfStarterException("Issue rendering help message", e);
        }
    }

    // Private methods

    private void loadStarters() throws IOException {
        loadStarter(STARTER);  // Make dynamic in future when more starters are added
    }

    private void loadStarter(String starterId) throws IOException {
        String starterFilename = "starters/" + starterId + ".starter.yml";
        InputStream inputStream = this.getClass().getClassLoader().getResource(starterFilename).openStream();
        VfStarter starter = YAML_MAPPER.readValue(inputStream, VfStarter.class);
        starter.setId(starterId);

        // Validate and if valid then init templates and insert into map
        validate(starter);
        initParameters(starter);
        initTemplates(starter);
        this.starters.put(STARTER, starter);
    }

    private void validate(VfStarter starter) {
        // todo
    }

    private void initParameters(VfStarter starter) {
        if (starter.getParameters() == null) {
            return;
        }
        for (Entry<String, VfStarterParam> entry : starter.getParameters().entrySet()) {
            String id = entry.getKey();
            VfStarterParam param = entry.getValue();
            param.setId(id);
            if (param.getName() == null) { // If name is null then set to id
                String name = id.replaceAll("-", " ");
                param.setName(name);
            }
            if (param.getValidation() != null) {
                initParameterValidation(starter, param);
            }
        }
    }

    private void initParameterValidation(VfStarter starter, VfStarterParam param) {
        VfStarterParamValidation validation = param.getValidation();
        if (validation.getMatch() != null) {
            try {
                Pattern matchRegex = Pattern.compile(validation.getMatch());
                validation.setMatchRegex(matchRegex);
            } catch (PatternSyntaxException e) {
                throw new VfStarterException("Error parsing regex [ " + validation.getMatch() +
                    " ] for parameter id [ " + param.getId() + " ] in starter [ " + starter.getId() + " ]");
            }
        }
    }

    private void initTemplates(VfStarter starter) throws IOException {
        for (Entry<String, VfStarterFile> entry : starter.getFiles().entrySet()) {
            VfStarterFile starterFile = entry.getValue();
            String id = entry.getKey();

            // 1) Filename
            starterFile.setId(id);
            if (starterFile.getFilename() != null) {
                Template templateFilename = templateService.initTemplate(starterFile.getFilename());
                starterFile.setTemplateFilename(templateFilename);
            }

            // 2) File contents
            if (starterFile.getTemplate() != null) {
                Template templateContents = templateService.initTemplate(starterFile.getTemplate().trim());
                starterFile.setTemplateContents(templateContents);
            } else if (starterFile.getRaw() != null) {
                starterFile.setRaw(starterFile.getRaw().trim());
            } else if (starterFile.getBase64() != null) {
                // Parse Base64 to String
                String base64 = starterFile.getBase64().trim();
                byte[] binaryBytes = Base64.getDecoder().decode(base64);
                if (starterFile.isBinary()) {
                    starterFile.setBytes(binaryBytes);
                } else {
                    starterFile.setRaw(new String(binaryBytes, Charset.defaultCharset()));
                }
            } else {
                throw new VfStarterException("Please specify either [ template, plain, base64 ] for file [ " + id +
                    " ]");
            }
        }
    }

    private void validateParam(VfStarterParam starterParam, String paramValue) {
        VfStarterParamValidation paramValidation = starterParam.getValidation();
        if (paramValidation == null) { // no validation required
            return;
        }

        // Basic validation
        if (paramValidation.isRequired()) {
            if (paramValue == null || paramValue.trim().isEmpty()) {
                throw new VfStarterValidationException(
                    "Specify a value for parameter [ " + starterParam.getId() + " ]");
            }
        }

        // Regex validation
        if (paramValidation.getMatchRegex() != null) {
            Matcher matcher = paramValidation.getMatchRegex().matcher(paramValue);
            if (!matcher.find()) {
                throw new VfStarterValidationException(
                    "Specify a value for parameter [ " + starterParam.getId() + " ]");
            }
        }
    }

    private Map<String, Object> getTemplateModel(Map<String, String> parameters) {
        Map<String, Object> model = new HashMap<>();
        model.put("param", parameters);
        return model;
    }

}