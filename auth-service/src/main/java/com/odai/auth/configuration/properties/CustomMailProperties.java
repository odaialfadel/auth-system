package com.odai.auth.configuration.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail")
public class CustomMailProperties {

    @NotBlank
    private String baseUrl;

    @Email
    private String from;
    private VerifyMail verify;

    @Getter
    @Setter
    public static class VerifyMail {
        String serviceName;
    }
}
