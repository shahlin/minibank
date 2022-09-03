package com.example.minibank.exceptions;

public class CustomerIneligibleException extends RuntimeException {
    public CustomerIneligibleException(String message) {
        super(message);
    }
}
