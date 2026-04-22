package com.bank.bank.model.dto;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "Recipient account ID is required")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    private BigDecimal amount;
}
