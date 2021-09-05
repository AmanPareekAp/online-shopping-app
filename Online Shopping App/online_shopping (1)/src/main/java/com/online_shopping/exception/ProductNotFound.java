package com.online_shopping.exception;

public class ProductNotFound extends RuntimeException {

    public ProductNotFound(String message) {
        super(message);
    }
}
