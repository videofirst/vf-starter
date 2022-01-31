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
package io.videofirst.starter;

import io.micronaut.context.ApplicationContext;
import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.videofirst.starter.controller.api.service.ApiService;
import io.videofirst.starter.util.VfStarterUtils;
import java.util.Map;

/**
 * Main Application.  If run with no arguments, VF-Starter will run as a web application.  If arguments are supplied
 * then it can run as a CLI tool to generate a project to a specified output folder.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@OpenAPIDefinition(
    info = @Info(
        title = "API",
        description = "The quickest way to generate VFA (Video First Automation) projects",
        license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0.txt"),
        contact = @Contact(url = "https://start.videofirst.io", name = "Video First", email = "info@videofirst.io")
    )
)
public class Application {

    public static void main(String[] args) {
        if (args.length == 0) {
            // Default is to run as web application
            Micronaut.run(Application.class, args);
        } else {
            // If arguments exists, run as CLI (generates project to a folder - very useful when testing templates)
            generateProjectToFolder(args);
        }
    }

    // Private methods

    private static void generateProjectToFolder(String[] args) {
        // Validate arguments. NOTE: if this gets more complex we can use https://picocli.info
        if (args.length < 2 || args[0].contains("=") || args[1].contains("=")) {
            displayUsage();
            return;
        }

        // Extract (1) project, (2) output folder and (3) parameter value map from arguments
        String project = args[0];
        String outputFolder = args[1];
        Map<String, String> paramValues = VfStarterUtils.parseArgsToMap(args);

        // Retrieve ApiService from application context and create folder
        ApplicationContext applicationContext = ApplicationContext.run();
        ApiService service = applicationContext.getBean(ApiService.class);
        service.createToFolder(project, outputFolder, paramValues);
    }

    private static void displayUsage() {
        System.err.println("VF Starter - CLI");
        System.err.println();
        System.err.println("Usage:");
        System.err.println();
        System.err.println("    <project> <output_folder> [paramKey1=value1] [paramKey2=value2] [ ... ]");
        System.err.println();
        System.err.println("Parameters are optional, default parameter values from starter file are used if omitted");
    }

}
