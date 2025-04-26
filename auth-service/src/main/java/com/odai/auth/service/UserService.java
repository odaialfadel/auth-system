package com.odai.auth.service;

import com.odai.auth.model.User;

public interface UserService {
    User registerUser(String email, String firstName, String lastName);
    User getUserByKeycloakId(String keycloakId);
    void deactivateUser(Long userId);
}
