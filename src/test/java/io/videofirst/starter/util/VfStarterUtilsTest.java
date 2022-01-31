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
package io.videofirst.starter.util;

import static io.videofirst.starter.util.VfStarterUtils.parseArgsToMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.Test;

/**
 * Unit test to test the methods of VfStarterUtils class.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public class VfStarterUtilsTest {

    @Test
    void should_parseArgsToMap() {
        assertThat(parseArgsToMap("a=1", "b=2")).containsExactly(entry("a", "1"), entry("b", "2"));
        assertThat(parseArgsToMap()).isEmpty();
    }

}
