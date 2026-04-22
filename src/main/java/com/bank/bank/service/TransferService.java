package com.bank.bank.service;

import com.bank.bank.exception.AccountNotFoundException; // ✅ Наш RuntimeException!
import com.bank.bank.model.entity.Account;
import com.bank.bank.model.entity.Transaction;
import com.bank.bank.model.enums.TransactionStatus;
import com.bank.bank.exception.InsufficientBalanceException;
import com.bank.bank.exception.SelfTransferException;
import com.bank.bank.repository.AccountRepository;
import com.bank.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    @Transactional(rollbackFor = Exception.class)
    public Transaction executeTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        if (fromAccountId.equals(toAccountId)) {
            throw new SelfTransferException("Нельзя переводить на свой счёт");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма должна быть больше 0");
        }

        Account from = accountRepository.findByIdWithLock(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Счёт отправителя не найден"));
        Account to = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Счёт получателя не найден"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Недостаточно средств на счёте");
        }

        from.debit(amount);
        to.credit(amount);
        accountRepository.saveAll(List.of(from, to));

        Transaction tx = Transaction.builder()
                .fromAccount(from)
                .toAccount(to)
                .amount(amount)
                .status(TransactionStatus.SUCCESS)
                .build();

        Transaction saved = transactionRepository.save(tx);
        log.info("✅ Перевод выполнен: {} -> {} | Сумма: {} | ID: {}", fromAccountId, toAccountId, amount, saved.getId());
        return saved;
    }
}
