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

import static freemarker.template.Configuration.VERSION_2_3_31;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import io.micronaut.context.annotation.Context;
import io.videofirst.starter.configuration.properties.VfStarterConfigProps;
import io.videofirst.starter.exceptions.VfStarterException;
import jakarta.inject.Inject;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Template rendering service which takes parameter models and uses Freemarker to render content.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Context
public class TemplateRenderService {

    private Configuration freemarkerConfiguration = new Configuration(VERSION_2_3_31);

    private VfStarterConfigProps config;

    @Inject
    public TemplateRenderService(VfStarterConfigProps config) throws TemplateModelException {
        freemarkerConfiguration.setClassForTemplateLoading(getClass(), "/freemarker");
        freemarkerConfiguration.setDefaultEncoding("utf-8");
        freemarkerConfiguration.addAutoInclude("/global.ftl");
        freemarkerConfiguration.setNumberFormat("0.######");

        // Shared services
        Map<String, Object> sharedVariables = new HashMap<>();
        sharedVariables.put("config", config);
        freemarkerConfiguration.setSharedVariables(sharedVariables);
    }

    public Template initTemplate(String content) throws IOException {
        Template template = new Template(UUID.randomUUID().toString(),
            new StringReader(content), freemarkerConfiguration);
        return template;
    }

    public String renderTemplate(Template template, Map<String, Object> model) {
        if (template == null) {
            return null;
        }
        try {
            Writer out = new StringWriter();
            template.process(model, out);
            return out.toString();
        } catch (TemplateException | IOException e) {
            throw new VfStarterException("Issue rendering template", e);
        }
    }

    public String renderTemplate(String templateFilename, Map<String, Object> model) {
        try {
            Template template = freemarkerConfiguration.getTemplate("templates/" + templateFilename + ".ftl");
            return renderTemplate(template, model);
        } catch (Exception e) {
            throw new VfStarterException("Issue rendering help message", e);
        }
    }

}