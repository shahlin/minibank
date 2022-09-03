package com.example.minibank.exception;

public class CustomerEmailTakenException extends RuntimeException {
    public CustomerEmailTakenException() {
        super("Customer email is already taken");
    }
}
