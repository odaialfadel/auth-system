package com.odai.auth.exception;

public class InvalidOldPasswordException extends CustomException {
    public InvalidOldPasswordException(Object... args) {
        super("Old password is incorrect for username '{}'.", args);
    }
}
