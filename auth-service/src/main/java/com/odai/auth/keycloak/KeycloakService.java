package com.odai.auth.keycloak;

import com.odai.auth.exception.InvalidOldPasswordException;
import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KeycloakService {
    private final KeycloakAdminGateway keycloakAdminGateway;
    private final KeycloakAuthGateway  keycloakAuthGateway;

    /**
     * Creates a user in Keycloak.
     *
     * @param email     User's emailOrUsername (also used as username).
     * @param firstName User's first name.
     * @param lastName  User's last name.
     * @return keycloakId The ID of the created user.
     */
    public String RegisterNewUser(String email, String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);
        user.setEnabled(true);

        return keycloakAdminGateway.createUser(user);
    }

    public void deleteUser(String keycloakId) {
        keycloakAdminGateway.deleteUser(keycloakId);
    }

//    public void changePassword(String keycloakId, String username, String oldPassword, String newPassword) {
//        if (keycloakAuthGateway.verifyUserCredentials(username, oldPassword)) {
//            keycloakAdminGateway.changePassword(keycloakId, newPassword);
//        }
//        throw new InvalidOldPasswordException(username);
//    }
}
