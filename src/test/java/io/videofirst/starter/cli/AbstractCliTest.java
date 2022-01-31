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
package io.videofirst.starter.cli;

import io.videofirst.starter.AbstractIntegrationTest;
import io.videofirst.starter.exceptions.VfStarterException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract class which all CLI based tests should extend.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public abstract class AbstractCliTest extends AbstractIntegrationTest {

    protected ByteArrayOutputStream out;
    protected ByteArrayOutputStream err;

    @BeforeEach
    public void setup() {
        // Create byte output streams
        this.out = new ByteArrayOutputStream();
        this.err = new ByteArrayOutputStream();

        // Redirect System out / err to output streams, so we capture what goes to the console
        System.setOut(new PrintStream(this.out));
        System.setErr(new PrintStream(this.err));
    }

    // Protected output

    protected String getOutput() {
        String out = this.out.toString();
        String err = this.err.toString();

        System.out.println(out);
        System.out.println(err);

        return out + err;
    }

    protected void deleteDirectory(File folder) {
        try {
            FileUtils.deleteDirectory(folder);
        } catch (IOException ioEx) {
            throw new VfStarterException(ioEx);
        }
    }

}
