package com.odai.auth.configuration;

import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class JavaMailSenderConfiguration {

    private final MailProperties mailProperties;

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
