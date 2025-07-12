package com.odai.auth.controller.auth;

import com.odai.auth.gateway.keycloak.KeycloakAuthGateway;
import com.odai.auth.shared.dto.login.LoginRequest;
import com.odai.auth.shared.dto.login.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class LoginController {

    private final KeycloakAuthGateway keycloakAuthGateway;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AccessTokenResponse token = keycloakAuthGateway.getAccessToken(request.emailOrUsername(), request.password());
            log.info("Access Token obtained for: {} seconds", token.getExpiresIn());
            return ResponseEntity.ok(new LoginResponse(
                    token.getToken(),
                    token.getExpiresIn(),
                    token.getTokenType()));
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login failed");
        }
    }
}
