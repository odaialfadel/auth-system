package com.odai.auth.configuration;

import com.odai.auth.configuration.properties.KeycloakProperties;
import lombok.AllArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating a {@link Keycloak} client instance using application-defined properties.
 * <p>
 * This configuration sets up a {@code Keycloak} bean that can be injected wherever direct Keycloak Admin API access is needed.
 * It uses the {@code client_credentials} grant type to authenticate as a service client.
 */
@AllArgsConstructor
@Configuration
public class KeyCloakConfiguration {

    /**
     * Properties required to connect and authenticate with the Keycloak server.
     */
    private final KeycloakProperties keycloakProperties;

    /**
     * Creates and configures a {@link Keycloak} instance for use with the Admin REST API.
     * <p>
     * The client is authenticated using the {@code client_credentials} flow.
     *
     * @return a fully configured {@link Keycloak} bean
     */
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm(keycloakProperties.getRealm())
                .clientId(keycloakProperties.getClientId())
                .clientSecret(keycloakProperties.getClientSecret())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();
    }
}

