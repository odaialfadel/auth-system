package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import com.odai.auth.gateway.keycloak.KeycloakAuthGateway;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class KeycloakAuthGatewayTest {

    private static KeycloakAuthGateway gateway;

    @Container
    private static final KeycloakContainer KEYCLOAK_CONTAINER = new KeycloakContainer("quay.io/keycloak/keycloak:26.2")
            .withRealmImportFile("realm-test.json")
            .withEnv("KC_DB", "dev-file")
            .withEnv("KEYCLOAK_ADMIN", "admin")
            .withEnv("KEYCLOAK_ADMIN_PASSWORD", "admin")
            .withCustomCommand("start");

    private static final String TEST_USERNAME = "odaii";
    private static final String TEST_PASSWORD = "secret";

    @BeforeAll
    static void setUp() {
        MailProperties mailProperties = new MailProperties();
        mailProperties.setHost("localhost");
        mailProperties.setPort(1025);
        mailProperties.setUsername("");
        mailProperties.setPassword("");
        mailProperties.setProtocol("smtp");


        Map<String, String> propertiesMap = new HashMap<>();
        propertiesMap.put("mail.smtp.auth", "false");
        propertiesMap.put("mail.smtp.starttls.enable", "false");

        mailProperties.getProperties().putAll(propertiesMap);


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
        long expiresIn = token.getExpiresIn();
        assertTrue(expiresIn <= 360, "Token expiration should not exceed 360 seconds: " + expiresIn);
    }

    @Test
    void shouldFailForInvalidCredentials() {
        assertThrows(Exception.class, () ->
                        gateway.getAccessToken("invalid-user", "wrong-password"),
                "Should throw Exception for invalid credentials");
    }
}