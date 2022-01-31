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
package io.videofirst.starter.exceptions;

/**
 * Main VF Starter exception (run-time exception) class which others should extend from in this project.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public class VfStarterException extends RuntimeException {

    public VfStarterException(String message) {
        super(message);
    }

    public VfStarterException(Throwable cause) {
        super(cause);
    }

    public VfStarterException(String message, Throwable cause) {
        super(message, cause);
    }

}
