package com.odai.auth.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuration class for setting up the {@link JavaMailSender} bean.
 * <p>
 * This class uses Spring Boot's {@link MailProperties} to configure a {@link JavaMailSenderImpl}
 * with the necessary SMTP server settings (host, port, username, password, and additional properties).
 * <p>
 * The configured {@code JavaMailSender} can then be injected and used for sending emails across the application.
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class JavaMailSenderConfiguration {

    /**
     * Spring Boot's built-in mail properties, automatically bound from {@code application.yml}
     * under the {@code spring.mail} prefix.
     */
    private final MailProperties mailProperties;

    /**
     * Creates and configures a {@link JavaMailSenderImpl} instance using the provided {@link MailProperties}.
     *
     * @return a fully configured {@link JavaMailSender} bean
     */
    @Bean
    public JavaMailSender getJavaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailProperties.getHost());
        javaMailSender.setPort(mailProperties.getPort());
        javaMailSender.setUsername(mailProperties.getUsername());
        javaMailSender.setPassword(mailProperties.getPassword());

        Properties javaMailProps = new Properties();
        javaMailProps.putAll(mailProperties.getProperties());

        javaMailSender.setJavaMailProperties(javaMailProps);
        return javaMailSender;
    }
}

