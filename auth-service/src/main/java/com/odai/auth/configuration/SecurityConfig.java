package com.odai.auth.configuration;

import lombok.NonNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Security configuration class for a Spring Boot application.
 * <p>
 * This configuration conditionally sets up two different security filter chains based on the
 * application property `authentication.active`. It also configures CORS settings for cross-origin
 * requests, typically from a frontend like Angular running on <code><a href="http://localhost:4200">...</a></code>.
 * </p>
 *
 * <p>
 * - If <code>authentication.active=true</code> (or missing), a secured filter chain requiring authentication
 * for API requests is applied. JWT-based authentication is used as an OAuth2 resource server.
 * </p>
 * <p>
 * - If <code>authentication.active=false</code>, a permissive filter chain is used allowing all requests without authentication.
 * </p>
 *
 * <p>
 * CORS is enabled in both modes to allow communication between frontend and backend.
 * </p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Defines the security filter chain that is activated when authentication is enabled.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return a {@link SecurityFilterChain} enforcing authentication for "/api/**" endpoints
     * @throws Exception in case of configuration errors
     */
    @Bean
    @ConditionalOnProperty(name = "authentication.active", havingValue = "true", matchIfMissing = true)
    public SecurityFilterChain authenticatedFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                        request -> request
                                .requestMatchers("/api/**")
                                .authenticated()
                                .anyRequest()
                                .permitAll())
                .oauth2ResourceServer(
                        oauth2 -> oauth2
                                .jwt(Customizer.withDefaults()));
        return http.build();
    }

    /**
     * Defines the security filter chain that is activated when authentication is disabled.
     * All incoming requests are allowed without authentication.
     *
     * @param http the {@link HttpSecurity} to modify
     * @return a {@link SecurityFilterChain} that permits all requests
     * @throws Exception in case of configuration errors
     */
    @Bean
    @ConditionalOnProperty(name = "authentication.active", havingValue = "false")
    public SecurityFilterChain unauthenticatedFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(
                request -> request
                        .anyRequest()
                        .permitAll());
        return http.build();
    }

    /**
     * Configures Cross-Origin Resource Sharing (CORS) to allow frontend applications
     * (e.g., running on <a href="http://localhost:4200">...</a>) to communicate with the backend.
     *
     * @return a {@link WebMvcConfigurer} bean that adds CORS mappings for all paths
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(@NonNull CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
