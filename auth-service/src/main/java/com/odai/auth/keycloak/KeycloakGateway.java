package com.odai.auth.keycloak;

import com.odai.auth.configuration.properties.KeycloakProperties;
import com.odai.auth.exception.keycloak.KeycloakGatewayException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.stereotype.Component;

@Component
public class KeycloakGateway {
    private static final String LOCATION = "Location";
    private static final char SLASH = '/';

    private final RealmResource realmResource;

    public KeycloakGateway(KeycloakProperties keycloakProperties, Keycloak keycloak) {
        this.realmResource = keycloak.realm(keycloakProperties.getRealm());
    }

    public String createUser(UserRepresentation user) {
        try (Response response = realmResource.users().create(user)) {
            if (response.getStatus() != 201) {
                throw new KeycloakGatewayException("Keycloak user creation failed for email={} with status={}",
                        user.getEmail(), response.getStatus());
            }
            return getKeycloakUserIdFromResponse(response);
        }
    }

    private static String getKeycloakUserIdFromResponse(Response response) {
        String locationHeader = response.getHeaderString(LOCATION);
        return locationHeader.substring(locationHeader.lastIndexOf(SLASH) + 1);
    }
}
