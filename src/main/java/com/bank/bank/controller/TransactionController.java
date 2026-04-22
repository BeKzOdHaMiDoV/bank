package com.bank.bank.controller;



import com.bank.bank.model.dto.TransactionResponse;
import com.bank.bank.security.UserDetailsImpl;
import com.bank.bank.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getHistory(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        // ✅ Сервис уже возвращает Page<TransactionResponse>, мапить не нужно!
        Page<TransactionResponse> txPage = transactionService.getHistoryByUserId(
                userId,
                org.springframework.data.domain.PageRequest.of(page, size,
                        org.springframework.data.domain.Sort.by("createdAt").descending())
        );

        return ResponseEntity.ok(txPage); // ✅ Просто возвращаем
    }
}