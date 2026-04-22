package com.bank.bank.validation;


import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class AmountValidator {

    /**
     * Проверяет, что сумма не null и строго больше 0.
     */
    public boolean isValid(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
