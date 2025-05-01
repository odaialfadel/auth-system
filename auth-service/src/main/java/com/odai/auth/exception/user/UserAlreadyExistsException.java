package com.odai.auth.exception.user;

import com.odai.auth.exception.CustomException;

public class UserAlreadyExistsException extends CustomException {
    public UserAlreadyExistsException(String messageTemplate) {
        super(messageTemplate);
    }

    public UserAlreadyExistsException(String messageTemplate, Throwable cause) {
        super(messageTemplate, cause);
    }

    public UserAlreadyExistsException() {
        super("A user with the given identifier already exists.");
    }
}
