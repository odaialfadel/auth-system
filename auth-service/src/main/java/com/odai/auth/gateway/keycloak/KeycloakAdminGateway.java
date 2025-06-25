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

@Component
public class KeycloakAdminGateway {
    private static final String LOCATION = "Location";
    private static final char SLASH = '/';

    private final RealmResource realmResource;

    public KeycloakAdminGateway(KeycloakProperties keycloakProperties, Keycloak keycloak) {
        this.realmResource = keycloak.realm(keycloakProperties.getRealm());
    }

    public String createUser(UserRepresentation user) {
        Response response = realmResource.users().create(user);
        return handleResponse(response, HttpStatus.SC_CREATED, () -> getKeycloakUserIdFromResponse(response),
                "Keycloak user creation failed for emailOrUsername '{}' with status '{}'", user.getEmail(), response.getStatus());
    }

    public void sendVerificationMail(String keycloakId, List<String> requiredActions) {
        getUserResourceByKeycloakId(keycloakId).executeActionsEmail(requiredActions);
    }

    private static String getKeycloakUserIdFromResponse(Response response) {
        String locationHeader = response.getHeaderString(LOCATION);
        return locationHeader.substring(locationHeader.lastIndexOf(SLASH) + 1);
    }

    public UserRepresentation getUserByKeycloakId(String keycloakId) {
        try {
            return getUserResourceByKeycloakId(keycloakId).toRepresentation();
        } catch (NotFoundException ex) {
            throw new UserNotFoundException(keycloakId, ex);
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to retrieve Keycloak user with ID '{}'", keycloakId, ex);
        }
    }

    public UserRepresentation getUserByEmail(String email) {
        List<UserRepresentation> users = realmResource.users().search(email, true);
        return users.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst().orElseThrow(() -> new UserNotFoundException(email));
    }

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

    public void resetPassword(String keycloakId) {
        try {
            getUserResourceByKeycloakId(keycloakId).executeActionsEmail(List.of(KeycloakConstants.UPDATE_PASSWORD));
        } catch (Exception ex) {
            throw new KeycloakGatewayException("Failed to send password reset emailOrUsername to user with id '{}'", ex, keycloakId);
        }
    }

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

    public void assignToGroup(String keycloakId, String groupName) {
        GroupRepresentation group = realmResource.groups().groups().stream()
                .filter(g -> g.getName().equalsIgnoreCase(groupName))
                .findFirst()
                .orElseThrow(() -> new KeycloakGatewayException("Failed to find group with name '{}'", groupName));
        GroupResource groupResource = realmResource.groups().group(group.getId());
        getUserResourceByKeycloakId(keycloakId).joinGroup(groupResource.toRepresentation().getId());
    }

    private UserResource getUserResourceByKeycloakId(String keycloakId) {
        return realmResource.users().get(keycloakId);
    }

    public void deleteUser(String userId) {
        Response response = realmResource.users().delete(userId);
        handleResponse(response, HttpStatus.SC_NO_CONTENT,
                "Keycloak user deletion failed for user id '{}' with status '{}'", userId, response.getStatus());
    }

    private <T> T handleResponse(Response response, int expectedStatus, Supplier<T> onSuccess, String errorMessageTemplate, Object... errorArgs) {
        try (response) {
            if (response.getStatus() != expectedStatus) {
                throw new KeycloakGatewayException(errorMessageTemplate, errorArgs);
            }
            return onSuccess.get();
        }
    }

    private void handleResponse(Response response, int expectedStatus, String errorMessageTemplate, Object... errorArgs) {
        try (response) {
            if (response.getStatus() != expectedStatus) {
                throw new KeycloakGatewayException(errorMessageTemplate, errorArgs);
            }
        }
    }
}
