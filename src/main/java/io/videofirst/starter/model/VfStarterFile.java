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
package io.videofirst.starter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import freemarker.template.Template;
import lombok.Data;

/**
 * Model class which holds each file item in a starter file.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Data
public class VfStarterFile {

    private String id;          // This is the filename is `filename` isn't set
    private String filename;

    private String raw;         // raw content (no templating support)
    private String template;    // template content (using Freemarker)
    private String base64;      // binary file which is base64 encoded

    private boolean executable; // executable (sets permissions)
    private boolean selected;   // initially selected file e.g. when previewing files
    private boolean binary;     // if true, don't return when previewing files

    // Internal

    @JsonIgnore
    private byte[] bytes;      // raw binary contents

    @JsonIgnore
    private Template templateFilename;

    @JsonIgnore
    private Template templateContents;

}
