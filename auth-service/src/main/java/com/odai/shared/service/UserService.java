package com.odai.shared.service;

import com.odai.shared.model.User;

public interface UserService {
    User registerUser(String email, String firstName, String lastName);
    User getUserByKeycloakId(String keycloakId);
    void deactivateUser(Long userId);
}
