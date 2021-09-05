package com.online_shopping.exception;

public class AddressNotFoundException extends RuntimeException{

    public AddressNotFoundException(String message)
    {
        super(message);
    }

}
