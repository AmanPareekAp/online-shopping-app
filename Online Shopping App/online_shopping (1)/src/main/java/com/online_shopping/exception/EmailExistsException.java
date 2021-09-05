package com.online_shopping.exception;

@SuppressWarnings("serial")
public class EmailExistsException extends RuntimeException {

    public EmailExistsException(final String message) {
        super(message);
    }

}
