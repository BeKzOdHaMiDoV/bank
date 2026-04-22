package com.bank.bank.controller;


import com.bank.bank.model.dto.AccountResponse;
import com.bank.bank.model.dto.TransferRequest;
import com.bank.bank.security.UserDetailsImpl;
import com.bank.bank.service.AccountService;
import com.bank.bank.service.TransferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final TransferService transferService;

    @GetMapping("/account")
    public ResponseEntity<AccountResponse> getBalance(Authentication auth) {
        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        return ResponseEntity.ok(accountService.getBalanceByUserId(userId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@Valid @RequestBody TransferRequest req, Authentication auth) {
        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        Long fromAccountId = accountService.getAccountIdByUserId(userId);
        var tx = transferService.executeTransfer(fromAccountId, req.getToAccountId(), req.getAmount());
        return ResponseEntity.ok("Transfer successful. Transaction ID: " + tx.getId());
    }
}
