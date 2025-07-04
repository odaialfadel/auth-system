package com.odai.auth.gateway.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import com.odai.auth.exception.KeycloakGatewayException;
import com.odai.auth.exception.UserNotFoundException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.apache.http.HttpStatus;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.GroupResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

/**
 * Gateway component responsible for interacting with the Keycloak Admin REST API.
 * <p>
 * This class encapsulates user management functionality such as creating users,
 * assigning roles or groups, sending verification or password reset emails, and more.
 * It is initialized with the realm context defined in {@link KeycloakProperties}.
 * </p>
 */
@Component
public class KeycloakAdminGateway {
    private static final String LOCATION = "Location";
    private static final char SLASH = '/';

    private final RealmResource realmResource;

    public KeycloakAdminGateway(KeycloakProperties keycloakProperties, Keycloak keycloak) {
        this.realmResource = keycloak.realm(keycloakProperties.getRealm());
    }

    /**
     * Creates a new Keycloak user.
     *
     * @param user the user representation to create
     * @return the created Keycloak user ID
     * @throws KeycloakGatewayException if creation fails
     */
    public String createUser(UserRepresentation user) {
        Response response = realmResource.users().create(user);
        return handleResponse(response, HttpStatus.SC_CREATED, () -> getKeycloakUserIdFromResponse(response),
                "Keycloak user creation failed for emailOrUsername '{}' with status '{}'", user.getEmail(), response.getStatus());
    }

    /**
     * Sends a verification email to the given user.
     *
     * @param keycloakId the Keycloak user ID
     * @param requiredActions list of required actions (e.g., VERIFY_EMAIL)
     */
    public void sendVerificationMail(String keycloakId, List<String> requiredActions) {
        getUserResourceByKeycloakId(keycloakId).executeActionsEmail(requiredActions);
    }

    /**
     * Parses the user ID from the Keycloak creation response's Location header.
     *
     * @param response the HTTP response
     * @return the extracted Keycloak user ID
     */
    private static String getKeycloakUserIdFromResponse(Response response) {
        String locationHeader = response.getHeaderString(LOCATION);
        return locationHeader.substring(locationHeader.lastIndexOf(SLASH) + 1);
    }

    /**
     * Retrieves user details by Keycloak ID.
     *
     * @param keycloakId the Keycloak user ID
     * @return the user representation
     * @throws UserNotFoundException if the user is not found
     * @throws KeycloakGatewayException for other errors
     */
    public UserRepresentation getUserByKeycloakId(String keycloakId) {
        try {
            return getUserResourceByKeycloakId(keycloakId).toRepresentation();
        } catch (NotFoundException ex) {
            throw new UserNotFoundException(keycloakId, ex);
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to retrieve Keycloak user with ID '{}'", keycloakId, ex);
        }
    }

    /**
     * Retrieves a user by email address.
     *
     * @param email the email address to search
     * @return the user representation
     * @throws UserNotFoundException if no user with the given email is found
     */
    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> users = realmResource.users().search(email, true);
        return users.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst().orElseThrow(() -> new UserNotFoundException(email));
    }

    /**
     * Changes the user's password directly (without requiring confirmation).
     *
     * @param keycloakId the Keycloak user ID
     * @param newPassword the new password to set
     */
    public void changePassword(String keycloakId, String newPassword) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(newPassword);
        credential.setTemporary(false);

        try {
            getUserResourceByKeycloakId(keycloakId).resetPassword(credential);
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to reset password for keycloak id '{}'", keycloakId, ex);
        }
    }

    /**
     * Sends a password reset email to the user.
     *
     * @param keycloakId the Keycloak user ID
     */
    public void resetPassword(String keycloakId) {
        try {
            getUserResourceByKeycloakId(keycloakId).executeActionsEmail(List.of(KeycloakConstants.UPDATE_PASSWORD));
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to send password reset emailOrUsername to user with id '{}'", ex, keycloakId);
        }
    }

    /**
     * Assigns a realm-level role to a user.
     *
     * @param keycloakId the Keycloak user ID
     * @param roleName the name of the role to assign
     */
    public void assignRole(String keycloakId, String roleName) {
        try {
            RoleRepresentation role = realmResource.roles().get(roleName).toRepresentation();
            getUserResourceByKeycloakId(keycloakId).roles().realmLevel().add(List.of(role));
        } catch (NotFoundException ex) {
            throw new UserNotFoundException(keycloakId, ex);
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to assign role '{}' to user '{}'", roleName, keycloakId, ex);
        }
    }

    /**
     * Assigns a user to a Keycloak group.
     *
     * @param keycloakId the Keycloak user ID
     * @param groupName the name of the group
     */
    public void assignToGroup(String keycloakId, String groupName) {
        GroupRepresentation group = realmResource.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElseThrow(() -> new KeycloakGatewayException("Failed to find group with name '{}'", groupName));
        GroupResource groupResource = realmResource.groups().group(group.getId());
        getUserResourceByKeycloakId(keycloakId).joinGroup(groupResource.toRepresentation().getId());
    }


    /**
     * Retrieves a {@link UserResource} object by user ID.
     *
     * @param keycloakId the Keycloak user ID
     * @return the user resource
     */
    private UserResource getUserResourceByKeycloakId(String keycloakId) {
        return realmResource.users().get(keycloakId);
    }

    /**
     * Deletes a Keycloak user.
     *
     * @param userId the Keycloak user ID to delete
     */
    public void deleteUser(String userId) {
        Response response = realmResource.users().delete(userId);
        handleResponse(response, HttpStatus.SC_NO_CONTENT,
                "Keycloak user deletion failed for user id '{}' with status '{}'", userId, response.getStatus());
    }

    /**
     * Handles a Keycloak API response and returns a result if the response matches the expected status.
     *
     * @param response the HTTP response
     * @param expectedStatus the expected status code
     * @param onSuccess the action to execute on success
     * @param errorMessageTemplate the error message to use if the response fails
     * @param errorArgs the error arguments for formatting
     * @param <T> the type of the return value
     * @return the result of the success supplier
     * @throws KeycloakGatewayException if the response status does not match the expected status
     */
    private <T> T handleResponse(Response response, int expectedStatus, Supplier<T> onSuccess, String errorMessageTemplate, Object... errorArgs) {
        try (response) {
            if (response.getStatus() != expectedStatus) {
                throw new KeycloakGatewayException(errorMessageTemplate, errorArgs);
            }
            return onSuccess.get();
        }
    }

    /**
     * Handles a Keycloak API response without returning a result.
     *
     * @param response the HTTP response
     * @param expectedStatus the expected status code
     * @param errorMessageTemplate the error message to use if the response fails
     * @param errorArgs the error arguments for formatting
     * @throws KeycloakGatewayException if the response status does not match the expected status
     */
    private void handleResponse(Response response, int expectedStatus, String errorMessageTemplate, Object... errorArgs) {
        try (response) {
            if (response.getStatus() != expectedStatus) {
                throw new KeycloakGatewayException(errorMessageTemplate, errorArgs);
            }
        }
    }
}
