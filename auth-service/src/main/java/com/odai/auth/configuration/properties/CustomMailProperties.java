package com.odai.auth.configuration.properties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for email-related settings used in the application.
 * <p>
 * These properties are defined in the {@code application.yml} file under the {@code mail} prefix.
 * They include sender information, base URL for verification links, and metadata for verification emails.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "mail")
public class CustomMailProperties {

    /**
     * The base URL used to construct verification links in emails.
     * <p>
     * Example: {@code https://auth.example.com}
     */
    @NotBlank
    private String baseUrl;

    /**
     * The email address used as the "From" address in all outgoing emails.
     * <p>
     * Example: {@code no-reply@example.com}
     */
    @Email
    private String from;

    /**
     * Properties specific to verification-related emails.
     */
    private VerifyMail verify;

    /**
    * Nested class containing metadata for verification emails.
    */
    @Getter
    @Setter
    public static class VerifyMail {

        /**
         * The name of the service or application used in the email subject or content.
         * <p>
         * Example: {@code Auth-System}
         */
        String serviceName;
    }
}
