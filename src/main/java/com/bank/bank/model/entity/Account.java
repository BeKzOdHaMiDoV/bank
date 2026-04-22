package com.bank.bank.model.entity;

import com.bank.bank.exception.InsufficientBalanceException;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal balance = BigDecimal.ZERO;

    /** Списание с проверкой баланса */
    public void debit(BigDecimal amount) {
        if (this.balance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Недостаточно средств на счёте");
        }
        this.balance = this.balance.subtract(amount);
    }

    /** Зачисление */
    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
