package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import com.odai.auth.gateway.keycloak.KeycloakAuthGateway;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class KeycloakAuthGatewayTest {

    private KeycloakAuthGateway gateway;

    @Container
    private static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer("quay.io/keycloak/keycloak:26.2")
            .withRealmImportFile("realm-test.json")
            .withEnv("KC_DB", "dev-file")
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCustomCommand("start");

    private static final String TEST_USERNAME = "odaii";
    private static final String TEST_PASSWORD = "secret";

    @BeforeEach
    void setUp() {
        KeycloakProperties properties = new KeycloakProperties();
        properties.setServerUrl(KEYCLOAK_CONTAINER.getAuthServerUrl());
        properties.setRealm("test-realm");
        properties.setClientId("test-client");
        properties.setClientSecret("client-secret");

        gateway = new KeycloakAuthGateway(properties);
    }


    @Test
    void shouldObtainAccessTokenForValidUser() {
        AccessTokenResponse token = gateway.getAccessToken(TEST_USERNAME, TEST_PASSWORD);

        assertNotNull(token, "Token response should not be null");
        assertNotNull(token.getToken(), "Access token should not be null");
        assertTrue(token.getExpiresIn() >= 300 && token.getExpiresIn() <= 360,
                "Token expiration should be between 300 and 360 seconds (default 5 minutes)");
    }

    @Test
    void shouldFailForInvalidCredentials() {
        assertThrows(Exception.class, () ->
                        gateway.getAccessToken("invalid-user", "wrong-password"),
                "Should throw Exception for invalid credentials");
    }
}