package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@AllArgsConstructor
@Component
public class KeycloakAuthGateway {
    private final RestClient restClient;
    private final KeycloakProperties keycloakProperties;

    /**
     * Verifies user credentials against Keycloak server.
     *
     * @param username the username to verify
     * @param password the password to verify
     * @return true if credentials are valid, false if authentication fails
     */
    public boolean verifyUserCredentials(String username, String password) {
        URI tokenEndpoint = UriComponentsBuilder.fromUri(URI.create(keycloakProperties.getServerUrl()))
                .pathSegment("realms", keycloakProperties.getRealm(), "protocol", "openid-connect", "token")
                .build()
                .toUri();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", keycloakProperties.getClientId());
        form.add("client_secret", keycloakProperties.getClientSecret());
        form.add("username", username);
        form.add("password", password);

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(form)
                    .retrieve()
                    .toEntity(String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.warn("Failed to verify credentials for user '{}': {}", username, e.getStatusCode());
            return false;
        } catch (Exception e) {
            log.error("Unexpected error while verifying credentials for user '{}'", username, e);
            throw e;
        }
    }
}
