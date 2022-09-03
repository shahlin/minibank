package com.example.minibank.exceptions;

public class CustomerEmailTakenException extends RuntimeException {
    public CustomerEmailTakenException() {
        super("Customer email is already taken");
    }
}
