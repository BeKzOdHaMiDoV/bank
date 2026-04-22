package com.bank.bank.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class AccountResponse {
    private Long accountId;
    private BigDecimal balance;
}
