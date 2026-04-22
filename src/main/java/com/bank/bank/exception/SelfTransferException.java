package com.bank.bank.exception;


public class SelfTransferException extends ApiException {
    public SelfTransferException() {
        super("Нельзя переводить на свой счёт");
    }
    public SelfTransferException(String message) {
        super(message);
    }
}
