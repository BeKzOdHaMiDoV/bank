package com.bank.bank.service;



import com.bank.bank.model.dto.AccountResponse;
import com.bank.bank.model.entity.Account;
import com.bank.bank.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.bank.bank.exception.AccountNotFoundException;


@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountResponse getBalanceByUserId(Long userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for user"));
        return new AccountResponse(account.getId(), account.getBalance());
    }

    public Long getAccountIdByUserId(Long userId) {
        return accountRepository.findByUserId(userId)
                .map(Account::getId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
    }
}
