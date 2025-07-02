package com.odai.auth.service;

import com.odai.auth.shared.dto.registeration.UserRegistrationResponse;

import java.util.UUID;

public interface UserService {
    UserRegistrationResponse registerNewUser(String username, String firstName, String lastName, String email, String password);
    UserRegistrationResponse getUserByKeycloakId(UUID keycloakId);
    void deactivateUser(Long userId);
    void deleteUserById(Long userId);
}
