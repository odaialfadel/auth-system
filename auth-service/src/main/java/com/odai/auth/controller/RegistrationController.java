package com.odai.auth.controller;

import com.odai.auth.shared.dto.registeration.UserRegistrationRequest;
import com.odai.auth.model.User;
import com.odai.auth.service.UserService;
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
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest request) {
        User user = userService.registerNewUser(
                request.email(),
                request.firstName(),
                request.lastName()
        );
        log.info("User registered successfully: email= {}, id= {}", user.getEmail(), user.getId());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/keycloak/{keycloakId}")
    public ResponseEntity<User> getUserByKeycloakId(@PathVariable UUID keycloakId) {
        User user = userService.getUserByKeycloakId(keycloakId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Void> deactivateUser(@PathVariable Long userId) {
        userService.deactivateUser(userId);
        return ResponseEntity.noContent().build();
    }
}
