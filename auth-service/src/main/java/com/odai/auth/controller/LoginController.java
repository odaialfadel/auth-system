package com.odai.auth.controller;

import com.odai.auth.keycloak.KeycloakAuthGateway;
import com.odai.auth.shared.dto.login.LoginRequest;
import lombok.AllArgsConstructor;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final KeycloakAuthGateway keycloakAuthGateway;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AccessTokenResponse token = keycloakAuthGateway.getAccessToken(request.emailOrUsername(), request.password());
            return ResponseEntity.ok(token);
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body("Invalid credentials: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }
}
