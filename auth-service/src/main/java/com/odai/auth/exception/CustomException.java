package com.odai.auth.exception;

import org.slf4j.helpers.MessageFormatter;

public class CustomException extends RuntimeException {
    public CustomException(String messageTemplate, Object... args) {
        super(MessageFormatter.arrayFormat(messageTemplate, args).getMessage());
    }
}
