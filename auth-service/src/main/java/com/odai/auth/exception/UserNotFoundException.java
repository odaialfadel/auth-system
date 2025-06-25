package com.odai.auth.exception;

public class UserNotFoundException extends CustomException {
    public UserNotFoundException(String messageTemplate, Object... args) {
        super(messageTemplate, args);
    }

    public UserNotFoundException(Object identifier) {
        super("A user with the given identifier:{} does not exist.", identifier);
    }

    public UserNotFoundException(Object identifier, Throwable throwable) {
        super("A user with the given identifier:{} does not exist.", identifier, throwable);
    }
}
