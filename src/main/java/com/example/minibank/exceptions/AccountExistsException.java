package com.example.minibank.exceptions;

public class AccountExistsException extends RuntimeException {
    public AccountExistsException() {
        super("Account already exists for the customer");
    }
}
