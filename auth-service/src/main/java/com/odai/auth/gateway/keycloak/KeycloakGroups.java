package com.odai.auth.gateway.keycloak;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum KeycloakGroups {
    STANDARD_USERS("standard-users");

    private final String groupName;
}
