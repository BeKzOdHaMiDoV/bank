package com.bank.bank.exception;



public class InsufficientBalanceException extends ApiException {
    public InsufficientBalanceException() {
        super("Недостаточно средств на счёте");
    }
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
