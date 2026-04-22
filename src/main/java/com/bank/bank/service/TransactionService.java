package com.bank.bank.service;


import com.bank.bank.model.dto.TransactionResponse;
import com.bank.bank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public Page<TransactionResponse> getHistoryByUserId(Long userId, Pageable pageable) {
        return transactionRepository.findByAccountId(userId, pageable)
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getFromAccount().getId(),
                        tx.getToAccount().getId(),
                        tx.getAmount(),
                        tx.getStatus().name(),
                        tx.getCreatedAt()
                ));
    }

    public long countByUserId(Long userId) {
        // Простая агрегация: сколько транзакций прошло через этот аккаунт
        return transactionRepository.countByFromAccountIdOrToAccountId(userId, userId);
    }
}