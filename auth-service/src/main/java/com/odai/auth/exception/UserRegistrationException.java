package com.odai.auth.exception;

public class UserRegistrationException extends CustomException {
    public UserRegistrationException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }
}
