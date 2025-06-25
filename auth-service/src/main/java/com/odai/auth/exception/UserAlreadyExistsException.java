package com.odai.auth.exception;

public class UserAlreadyExistsException extends CustomException {
    public UserAlreadyExistsException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }

    public UserAlreadyExistsException(Object identifier) {
        super("A user with the given identifier: {} already exists.", identifier);
    }
}
