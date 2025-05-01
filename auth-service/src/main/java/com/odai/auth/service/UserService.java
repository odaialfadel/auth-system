package com.odai.auth.service;

import com.odai.auth.model.User;

import java.util.UUID;

public interface UserService {
    User registerNewUser(String email, String firstName, String lastName);
    User getUserByKeycloakId(UUID keycloakId);
    void deactivateUser(Long userId);
    void deleteUserById(Long userId);
}
