package com.bank.bank.repository;


import com.bank.bank.model.entity.Transaction;
import com.bank.bank.model.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Поиск транзакций, где пользователь был отправителем ИЛИ получателем
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId")
    Page<Transaction> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    // Только успешные исходящие транзакции
    @Query("SELECT t FROM Transaction t WHERE t.fromAccount.id = :accountId AND t.status = :status")
    List<Transaction> findSuccessfulByFromAccount(@Param("accountId") Long accountId,
                                                  @Param("status") TransactionStatus status);

    // Автоматически генерируется Spring Data
    long countByStatus(TransactionStatus status);

    // Подсчёт всех транзакций пользователя (через связанные аккаунты)
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAccount.user.id = :userId OR t.toAccount.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    // Подсчёт транзакций пользователя (как отправителя или получателя)
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.fromAccount.id = :accountId OR t.toAccount.id = :accountId")
    long countByFromAccountIdOrToAccountId(@Param("accountId") Long fromAccountId, @Param("accountId") Long toAccountId);
}
