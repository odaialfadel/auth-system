package com.odai.auth.exception.keycloak;

import com.odai.auth.exception.CustomException;

public class KeycloakGatewayException extends CustomException {
    public KeycloakGatewayException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }
}
