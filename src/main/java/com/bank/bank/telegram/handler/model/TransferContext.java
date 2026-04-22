package com.bank.bank.telegram.handler.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferContext {
    private TransferState state = TransferState.NONE;
    private BigDecimal amount;
    private Long toAccountId;
}
