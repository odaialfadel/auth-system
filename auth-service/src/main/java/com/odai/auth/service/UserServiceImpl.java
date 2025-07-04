package com.odai.auth.service;

import com.odai.auth.exception.UserAlreadyExistsException;
import com.odai.auth.exception.UserNotFoundException;
import com.odai.auth.gateway.keycloak.KeycloakService;
import com.odai.auth.domain.model.User;
import com.odai.auth.domain.repository.UserRepository;
import com.odai.auth.service.auth.EmailVerificationService;
import com.odai.auth.shared.dto.registeration.UserRegistrationResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Implementation of {@link UserService} that manages user registration,
 * retrieval, deactivation, and deletion by coordinating persistence with
 * {@link UserRepository}, Keycloak operations through {@link KeycloakService},
 * and email verification via {@link EmailVerificationService}.
 */
@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private static final String REGISTRATION_SUCCESSFUL_PLEASE_VERIFY_YOUR_EMAIL = "Registration successful! Please verify your email.";
    private static final String USER_FOUND = "User found.";

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final EmailVerificationService emailVerificationService;

    /**
     * Registers a new user if the email does not already exist.
     * <p>
     * The user is created in Keycloak via {@link KeycloakService} and
     * persisted locally in the database. A verification email is sent after registration.
     * </p>
     *
     * @param username  the username chosen by the user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     * @param password  the user's password
     * @return a {@link UserRegistrationResponse} containing user details and a success message
     * @throws UserAlreadyExistsException if a user with the given email already exists
     */
    @Transactional
    @Override
    public UserRegistrationResponse registerNewUser(String username, String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        UUID keycloakId = UUID.fromString(keycloakService.RegisterNewUser(username, firstName, lastName, email, password));

        User savedUser = userRepository.save(User.builder()
                .keycloakId(keycloakId)
                .username(username)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .build());

        UserRegistrationResponse userRegistrationResponse = new UserRegistrationResponse(
                savedUser.getId(), savedUser.getKeycloakId(), savedUser.getUsername(), savedUser.getEmail(),
                REGISTRATION_SUCCESSFUL_PLEASE_VERIFY_YOUR_EMAIL);

        emailVerificationService.sendVerificationMail(savedUser);

        return userRegistrationResponse;
    }

    /**
     * Retrieves a user by their Keycloak ID.
     *
     * @param keycloakId the Keycloak user ID
     * @return a {@link UserRegistrationResponse} with the user's details and a success message
     * @throws RuntimeException if the user is not found in the repository
     */
    @Override
    public UserRegistrationResponse getUserByKeycloakId(UUID keycloakId) {
        User user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserRegistrationResponse(user.getId(), user.getKeycloakId(), user.getUsername(), user.getEmail(),
                USER_FOUND);
    }

    /**
     * Deactivates a user account by setting its active flag to false.
     *
     * @param userId the database ID of the user to deactivate
     * @throws RuntimeException if the user is not found
     */
    @Override
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);
    }

    /**
     * Deletes a user both from the Keycloak server and the local repository.
     *
     * @param userId the database ID of the user to delete
     * @throws UserNotFoundException if the user is not found in the repository
     */
    @Override
    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        keycloakService.deleteUser(user.getKeycloakId().toString());
        userRepository.delete(user);
    }
}
