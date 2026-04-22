package com.bank.bank.exception;


public class AccountNotFoundException extends ApiException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
