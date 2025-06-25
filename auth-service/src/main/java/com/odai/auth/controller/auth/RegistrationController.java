package com.odai.auth.controller.auth;

import com.odai.auth.shared.dto.registeration.UserRegistrationRequest;
import com.odai.auth.service.auth.UserService;
import com.odai.auth.shared.dto.registeration.UserRegistrationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/users")
public class RegistrationController {
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserRegistrationResponse> registerUser(@RequestBody UserRegistrationRequest request) {
        UserRegistrationResponse createdUser = userService.registerNewUser(
                request.username(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.password()
        );
        log.info("User registered successfully: username= {}, id= {}", createdUser.username(), createdUser.userId());
        return ResponseEntity.ok(createdUser);
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<UserRegistrationResponse> getUserByKeycloakId(@PathVariable UUID keycloakId) {
        return ResponseEntity.ok(userService.getUserByKeycloakId(keycloakId));
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
