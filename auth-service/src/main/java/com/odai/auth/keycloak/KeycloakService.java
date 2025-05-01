package com.odai.auth.keycloak;

import lombok.AllArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KeycloakService {

    private KeycloakGateway keycloakGateway;

    /**
     * Creates a user in Keycloak.
     *
     * @param email     User's email (also used as username).
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

        return keycloakGateway.createUser(user);
    }
}
