package com.odai.auth.service;

import com.odai.auth.keycloak.KeycloakAdminClientService;
import com.odai.auth.model.User;
import com.odai.auth.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KeycloakAdminClientService keycloakAdminClientService;

    @Transactional
    @Override
    public User registerUser(String email, String firstName, String lastName) {
        UUID keycloakId = UUID.fromString(createUserInKeycloak(email, firstName, lastName));

        User user = new User();
        user.setKeycloakId(keycloakId);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        return userRepository.save(user);
    }

    @Override
    public User getUserByKeycloakId(UUID keycloakId) {
        return userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    private String createUserInKeycloak(String email, String firstName, String lastName) {
        return keycloakAdminClientService.createUser(email, firstName, lastName);
    }
}
