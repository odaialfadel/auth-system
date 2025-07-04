package com.odai.auth.gateway.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.stereotype.Component;

/**
 * Gateway component for authenticating users against Keycloak using the Resource Owner Password Credentials grant type.
 * <p>
 * This class is responsible for obtaining an access token for a user by providing their credentials.
 * </p>
 *
 * <p>
 * Note: This flow requires that the client has the `Direct Access Grants` enabled in Keycloak.
 * </p>
 */
@Slf4j
@AllArgsConstructor
@Component
public class KeycloakAuthGateway {
    private final KeycloakProperties keycloakProperties;

    /**
     * Retrieves an access token from Keycloak using the provided user credentials.
     *
     * @param emailOrUsername the email or username of the user
     * @param password the user's password
     * @return {@link AccessTokenResponse} containing the access token and related metadata
     * @throws RuntimeException if authentication fails or the token cannot be obtained
     */
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
