package com.odai.auth.service;

import com.odai.auth.exception.UserAlreadyExistsException;
import com.odai.auth.exception.UserNotFoundException;
import com.odai.auth.gateway.keycloak.KeycloakService;
import com.odai.auth.domain.model.User;
import com.odai.auth.domain.repository.UserRepository;
import com.odai.auth.shared.dto.registeration.UserRegistrationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String REGISTRATION_SUCCESSFUL_PLEASE_VERIFY_YOUR_EMAIL = "Registration successful! Please verify your email.";
    private static final String USER_FOUND = "User found.";

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;

    @Transactional
    @Override
    public UserRegistrationResponse registerNewUser(String username, String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        UUID keycloakId = UUID.fromString(keycloakService.RegisterNewUser(username, firstName, lastName, email, password));

        User saveUser = userRepository.save(User.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build());

        return new UserRegistrationResponse(
                saveUser.getId(), saveUser.getKeycloakId(), saveUser.getUsername(), saveUser.getEmail(),
                REGISTRATION_SUCCESSFUL_PLEASE_VERIFY_YOUR_EMAIL);
    }

    @Override
    public UserRegistrationResponse getUserByKeycloakId(UUID keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserRegistrationResponse(user.getId(), user.getKeycloakId(), user.getUsername(), user.getEmail(),
                USER_FOUND);
    }

    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        keycloakService.deleteUser(user.getKeycloakId().toString());
        userRepository.delete(user);
    }
}
