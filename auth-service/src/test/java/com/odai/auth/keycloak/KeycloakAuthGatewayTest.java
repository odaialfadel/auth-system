package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeycloakAuthGatewayTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec uriSpec;

    @Mock
    private RestClient.RequestBodySpec bodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Captor
    private ArgumentCaptor<URI> uriCaptor;

    @Captor
    private ArgumentCaptor<MultiValueMap<String, String>> formCaptor;

    private KeycloakAuthGateway gateway;
    private KeycloakProperties properties;

    private static final String TEST_USERNAME = "odai";
    private static final String TEST_PASSWORD = "secret";

    @BeforeEach
    void setUp() {
        properties = new KeycloakProperties();
        properties.setServerUrl("http://localhost:8080");
        properties.setRealm("test-realm");
        properties.setClientId("test-client");
        properties.setClientSecret("client-secret");

        gateway = new KeycloakAuthGateway(restClient, properties);

        // Setup chain mocking
        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(URI.class))).thenReturn(bodySpec);
        when(bodySpec.contentType(MediaType.APPLICATION_FORM_URLENCODED)).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void verifyUserCredentials_shouldReturnTrue_whenSuccessful() {
        // Arrange
        when(responseSpec.toEntity(String.class)).thenReturn(ResponseEntity.ok("{'access_token':'token'}"));

        // Act
        boolean result = gateway.verifyUserCredentials(TEST_USERNAME, TEST_PASSWORD);

        // Assert
        assertTrue(result);

        // Verify correct URI was used
        verify(uriSpec).uri(uriCaptor.capture());
        URI capturedUri = uriCaptor.getValue();
        assertEquals("http://localhost:8080/realms/test-realm/protocol/openid-connect/token",
                capturedUri.toString());

        // Verify form parameters
        verify(bodySpec).body(formCaptor.capture());
        MultiValueMap<String, String> form = formCaptor.getValue();
        assertEquals("password", form.getFirst("grant_type"));
        assertEquals(properties.getClientId(), form.getFirst("client_id"));
        assertEquals(properties.getClientSecret(), form.getFirst("client_secret"));
        assertEquals(TEST_USERNAME, form.getFirst("username"));
        assertEquals(TEST_PASSWORD, form.getFirst("password"));
    }

    @Test
    void verifyUserCredentials_shouldReturnFalse_onHttpClientError() {
        // Arrange
        when(bodySpec.retrieve()).thenThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED));

        // Act
        boolean result = gateway.verifyUserCredentials(TEST_USERNAME, "wrong-password");

        // Assert
        assertFalse(result);
    }

    @Test
    void verifyUserCredentials_shouldReturnFalse_onHttpServerError() {
        // Arrange
        when(bodySpec.retrieve()).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // Act
        boolean result = gateway.verifyUserCredentials(TEST_USERNAME, TEST_PASSWORD);

        // Assert
        assertFalse(result);
    }

    @Test
    void verifyUserCredentials_shouldRethrowOtherExceptions() {
        // Arrange
        RuntimeException exception = new RuntimeException("Network error");
        when(bodySpec.retrieve()).thenThrow(exception);

        // Act & Assert
        Exception thrown = assertThrows(RuntimeException.class, () ->
                gateway.verifyUserCredentials(TEST_USERNAME, TEST_PASSWORD)
        );
        assertSame(exception, thrown);
    }
}