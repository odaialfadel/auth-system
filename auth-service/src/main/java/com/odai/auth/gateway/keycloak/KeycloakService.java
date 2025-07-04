package com.odai.auth.gateway.keycloak;

import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service layer for managing Keycloak user operations.
 * <p>
 * This class acts as an abstraction over {@link KeycloakAdminGateway} and {@link KeycloakAuthGateway},
 * providing business-oriented methods for user registration, deletion, and (potentially) password changes.
 * </p>
 */
@AllArgsConstructor
@Service
public class KeycloakService {
    private final KeycloakAdminGateway keycloakAdminGateway;
    private final KeycloakAuthGateway  keycloakAuthGateway;

    /**
     * Registers a new user in Keycloak with default group assignment and verification email.
     * <p>
     * The user is assigned to the group <code>standard-users</code> and is required to verify their email.
     * </p>
     *
     * @param username  the username to use for login
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param email     the user's email address
     * @param password  the user's initial password
     * @return the Keycloak ID of the newly created user
     */
    public String RegisterNewUser(String username, String firstName, String lastName, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);

        // Set required actions before login
        List<String> requiredActions = List.of("VERIFY_EMAIL");
        user.setRequiredActions(requiredActions);

        CredentialRepresentation credential = createPasswordCredentials(password);
        user.setCredentials(List.of(credential));

        String keycloakId = keycloakAdminGateway.createUser(user);
        keycloakAdminGateway.assignToGroup(keycloakId, "standard-users");
        keycloakAdminGateway.sendVerificationMail(keycloakId, requiredActions);

        return keycloakId;
    }

    /**
     * Creates a credential object for password-based authentication.
     *
     * @param password the plain-text password
     * @return the {@link CredentialRepresentation} to attach to a Keycloak user
     */
    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
    }

    /**
     * Deletes a user from Keycloak by their ID.
     *
     * @param keycloakId the Keycloak user ID
     */
    public void deleteUser(String keycloakId) {
        keycloakAdminGateway.deleteUser(keycloakId);
    }

    /*
     * Changes a user's password after verifying their current one.
     *
     * @param keycloakId the Keycloak user ID
     * @param username the user's username or email
     * @param oldPassword the current password
     * @param newPassword the new password to set
     * @throws InvalidOldPasswordException if old credentials are invalid
     */
//    public void changePassword(String keycloakId, String username, String oldPassword, String newPassword) {
//        if (keycloakAuthGateway.verifyUserCredentials(username, oldPassword)) {
//            keycloakAdminGateway.changePassword(keycloakId, newPassword);
//        }
//        throw new InvalidOldPasswordException(username);
//    }
}
