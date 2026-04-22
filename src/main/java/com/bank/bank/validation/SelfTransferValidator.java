package com.bank.bank.validation;


import org.springframework.stereotype.Component;

@Component
public class SelfTransferValidator {

    /**
     * Проверяет, что ID отправителя и получателя не совпадают.
     */
    public boolean isValid(Long fromAccountId, Long toAccountId) {
        return fromAccountId != null && toAccountId != null && !fromAccountId.equals(toAccountId);
    }
}
