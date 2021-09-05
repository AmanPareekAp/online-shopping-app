package com.online_shopping.exception;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(final String message) {
        super(message);
    }
}
