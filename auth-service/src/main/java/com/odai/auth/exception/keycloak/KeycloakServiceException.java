package com.odai.auth.exception.keycloak;

import com.odai.auth.exception.CustomException;

public class KeycloakServiceException extends CustomException {
    public KeycloakServiceException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }
}
