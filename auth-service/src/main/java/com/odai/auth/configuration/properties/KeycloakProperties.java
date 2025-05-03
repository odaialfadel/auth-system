package com.odai.auth.configuration.properties;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakProperties {
    @NotBlank
    private String serverUrl;

    @NotBlank
    private String realm;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;
}
