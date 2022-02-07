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

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.runtime.server.EmbeddedServer;
import io.restassured.RestAssured;
import io.restassured.authentication.CertificateAuthSettings;
import io.videofirst.starter.AbstractIntegrationTest;
import org.junit.jupiter.api.BeforeEach;

/**
 * Abstract controller based integration test class for testing controller endpoints.
 *
 * @author Bob Marks
 * @since 2022.1
 */
public abstract class AbstractControllerTest extends AbstractIntegrationTest {

    protected EmbeddedServer server;

    public AbstractControllerTest(EmbeddedServer server) {
        this.server = server;
    }

    @BeforeEach
    public void setup() {
        if (RestAssured.port == server.getPort()) {
            return; // already set up
        }

        // Ensure server is OK by hitting `/health` endpoint
        try (HttpClient client = server.getApplicationContext()
            .createBean(HttpClient.class, server.getURL())) {
            assertEquals(HttpStatus.OK, client.toBlocking().exchange("/health").status());
        }

        // Set RestAssured baseURI, port and add self-signed cert to trust store for testing purposes.
        RestAssured.baseURI = server.getURL().toString();
        RestAssured.port = server.getPort();
        RestAssured.authentication =
            RestAssured.certificate(
                SSL_TRUST_STORE_NAME,
                SSL_TRUST_STORE_PASSWORD,
                SSL_TRUST_STORE_NAME,
                SSL_TRUST_STORE_PASSWORD,
                CertificateAuthSettings.certAuthSettings());
    }

}
