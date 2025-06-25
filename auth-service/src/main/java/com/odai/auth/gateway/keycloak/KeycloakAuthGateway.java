package com.odai.auth.gateway.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;

@Slf4j
@AllArgsConstructor
@Component
public class KeycloakAuthGateway {
    private final KeycloakProperties keycloakProperties;

    public AccessTokenResponse getAccessToken(String emailOrUsername, String password) {
       try( Keycloak keycloak = KeycloakBuilder.builder()
               .serverUrl(keycloakProperties.getServerUrl())
               .realm(keycloakProperties.getRealm())
               .clientId(keycloakProperties.getClientId())
               .clientSecret(keycloakProperties.getClientSecret())
               .username(emailOrUsername)
               .password(password)
               .grantType(OAuth2Constants.PASSWORD)
               .build()) {
           return keycloak.tokenManager().getAccessToken();
       } catch (Exception e) {
           log.error("Failed to obtain access token.",  e);
           throw e;
       }
    }
}
