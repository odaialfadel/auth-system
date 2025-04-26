package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class KeycloakAdminClientService {

    private KeycloakProperties keycloakProperties;
    private Keycloak keycloak;

    /**
     * Creates a user in Keycloak.
     *
     * @param email     User's email (also used as username).
     * @param firstName User's first name.
     * @param lastName  User's last name.
     * @return keycloakId The ID of the created user.
     */
    public String createUser(String email, String firstName, String lastName) {
        UserRepresentation user = new UserRepresentation();
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(email);
        user.setEnabled(true);

        Response response = keycloak.realm(keycloakProperties.getRealm())
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            throw new RuntimeException("Failed to create user: HTTP " + response.getStatus());
        }

        // Extract the user ID from the Location header
        String locationHeader = response.getHeaderString("Location");
        String userId = locationHeader.substring(locationHeader.lastIndexOf('/') + 1);

        response.close();
        return userId;
    }

}
