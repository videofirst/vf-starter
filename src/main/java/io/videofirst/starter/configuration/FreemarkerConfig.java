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
package io.videofirst.starter.configuration;

import freemarker.template.TemplateModelException;
import io.micronaut.context.annotation.Context;
import io.micronaut.context.annotation.Requires;
import io.micronaut.context.annotation.Value;
import io.micronaut.views.freemarker.FreemarkerViewsRendererConfigurationProperties;
import io.videofirst.starter.configuration.properties.VfStarterConfigProps;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to configure Freemarker Micronaut views and includes adding global variables to the Freemarker shared variables
 * map.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Context
@Requires(beans = FreemarkerViewsRendererConfigurationProperties.class)
public class FreemarkerConfig {

    @Value("${micronaut.context.path:`/`}")
    private String rootUrl;

    @Inject
    private FreemarkerViewsRendererConfigurationProperties freemarkerContextProps;

    @Inject
    private VfStarterConfigProps config;

    @PostConstruct
    public void setup() throws TemplateModelException {
        Map<String, Object> sharedVariables = new HashMap<>();
        sharedVariables.put("r", rootUrl);
        sharedVariables.put("now", LocalDateTime.now());
        sharedVariables.put("config", config);

        freemarkerContextProps.setSharedVariables(sharedVariables);
    }

}
