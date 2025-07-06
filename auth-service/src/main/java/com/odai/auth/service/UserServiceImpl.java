package com.odai.auth.service;

import com.odai.auth.exception.UserAlreadyExistsException;
import com.odai.auth.exception.UserNotFoundException;
import com.odai.auth.exception.UserRegistrationException;
import com.odai.auth.gateway.keycloak.KeycloakService;
import com.odai.auth.domain.model.User;
import com.odai.auth.domain.repository.UserRepository;
import com.odai.auth.service.auth.EmailVerificationService;
import com.odai.auth.shared.dto.registeration.UserRegistrationResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Implementation of {@link UserService} that manages user registration,
 * retrieval, deactivation, and deletion by coordinating persistence with
 * {@link UserRepository}, Keycloak operations through {@link KeycloakService},
 * and email verification via {@link EmailVerificationService}.
 */
@Slf4j
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
     * This method creates a user in Keycloak via {@link KeycloakService} and persists the user locally in the database.
     * A verification email is sent upon successful registration. The operation is wrapped in a transaction to ensure
     * data consistency. If any step fails (e.g., Keycloak creation, database save, or email verification), the method
     * performs a rollback by deleting the user from both Keycloak and the local database to prevent orphaned records.
     * </p>
     *
     * @param username  the username chosen by the user
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     * @param password  the user's password
     * @return a {@link UserRegistrationResponse} containing user details and a success message
     * @throws UserAlreadyExistsException if a user with the given email already exists
     * @throws UserRegistrationException  if user creation fails in Keycloak, local database, or during email verification
     */
    @Override
    @Transactional
    public UserRegistrationResponse registerNewUser(String username, String firstName, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        String keycloakUserId = null;
        User savedUser = null;

        try {
            keycloakUserId = keycloakService.registerNewUser(username, firstName, lastName, email, password);
            UUID keycloakId = UUID.fromString(keycloakUserId);

            savedUser = userRepository.save(User.builder()
                    .keycloakId(keycloakId)
                    .username(username)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .build());
            log.debug("User saved in local database with ID: {} for email: {}", savedUser.getId(), email);

            emailVerificationService.sendVerificationMail(savedUser);
            log.debug("Verification email sent for user with email: {}", email);

            log.info("User registration completed successfully for email: {}", email);
            return new UserRegistrationResponse(
                    savedUser.getId(), savedUser.getKeycloakId(), savedUser.getUsername(), savedUser.getEmail(),
                    REGISTRATION_SUCCESSFUL_PLEASE_VERIFY_YOUR_EMAIL);
        } catch (Exception e) {
            log.error("User registration failed for email: {}. Reason: {}", email, e.getMessage());
            // Rollback: Clean up if any step fails
            if (keycloakUserId != null) {
                // Delete user from Keycloak if created
                try {
                    keycloakService.deleteUser(keycloakUserId);
                    log.info("Rollback: Successfully deleted Keycloak user with ID: {} for email: {}", keycloakUserId, email);
                } catch (Exception keycloakCleanupException) {
                    log.error("Rollback: Failed to delete Keycloak user with ID: {} for email: {}. Error: {}",
                            keycloakUserId, email, keycloakCleanupException.getMessage());
                }
            }
            if (savedUser != null) {
                // Delete user from local database if created
                try {
                    userRepository.delete(savedUser);
                    log.info("User with ID {} deleted successfully.", savedUser.getId());
                } catch (Exception dbCleanupException) {
                    log.error("Rollback: Failed to delete local user with ID: {} for email: {}. Error: {}",
                            savedUser.getId(), email, dbCleanupException.getMessage());
                }
            }
            throw new UserRegistrationException("User registration failed for email: {}", email, e);
        }
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
