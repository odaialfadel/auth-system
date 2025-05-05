package com.odai.auth.gateway.keycloak;

import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

import java.util.List;

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

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);
        return credential;
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
