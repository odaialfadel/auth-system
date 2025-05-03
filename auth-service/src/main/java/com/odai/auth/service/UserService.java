package com.odai.auth.service;

import com.odai.auth.model.User;
import com.odai.auth.shared.dto.user.UserDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.UUID;

public interface UserService {
    User registerNewUser(String email, String firstName, String lastName);
    User getUserByKeycloakId(UUID keycloakId);
    void deactivateUser(Long userId);
    void deleteUserById(Long userId);

    UserDto getUserProfile(Jwt jwt);
}
