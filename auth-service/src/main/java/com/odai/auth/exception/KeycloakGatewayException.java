package com.odai.auth.exception;

public class KeycloakGatewayException extends CustomException {
    public KeycloakGatewayException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }
}
