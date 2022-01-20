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
import java.util.regex.Pattern;
import lombok.Data;

/**
 * Model class which holds parameter validation information in a starter file.
 *
 * @author Bob Marks
 * @since 2022.1
 */
@Data
public class VfStarterParamValidation {

    private boolean required;
    private String match;
    private String message;

    @JsonIgnore
    private Pattern matchRegex;

}
