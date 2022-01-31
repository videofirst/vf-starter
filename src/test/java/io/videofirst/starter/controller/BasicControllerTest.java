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
package io.videofirst.starter.controller;

import io.micronaut.runtime.server.EmbeddedServer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Basic integration test to test if the VF Starter App loads OK.  If this fails, then this should be fixed before
 * concentrating on the other integration tests.
 *
 * @author Bob Marks
 * @since 2022.1
 */
class BasicControllerTest extends AbstractControllerTest {

    public BasicControllerTest(EmbeddedServer server) {
        super(server);
    }

    @Test
    void should_load_server() {
        Assertions.assertTrue(server.isRunning());
    }

}
