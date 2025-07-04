package com.odai.auth.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for connecting to a Keycloak authentication server.
 * <p>
 * These properties are typically defined in the {@code application.yml} file under the {@code keycloak} prefix.
 * They are used to authenticate and interact with the Keycloak server (e.g. to request tokens or manage users).
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {

    /**
     * The base URL of the Keycloak server.
     * <p>
     * Example: {@code http://localhost:8081} or {@code https://keycloak.example.com}
     */
    @NotBlank
    private String serverUrl;

    /**
     * The name of the Keycloak realm to connect to.
     * <p>
     * Example: {@code auth_system}
     */
    @NotBlank
    private String realm;

    /**
     * The client ID used to authenticate against the Keycloak server.
     * <p>
     * This should match the client configured in the realm.
     * Example: {@code auth-client}
     */
    @NotBlank
    private String clientId;

    /**
     * The client secret used in combination with the client ID for authentication.
     * <p>
     * This must match the secret set in Keycloak for the given client.
     */
    @NotBlank
    private String clientSecret;
}
