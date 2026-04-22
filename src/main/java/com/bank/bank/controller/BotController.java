package com.bank.bank.controller;


import com.bank.bank.model.dto.AccountResponse;
import com.bank.bank.model.dto.TransferRequest;
import com.bank.bank.model.entity.User;
import com.bank.bank.repository.UserRepository;
import com.bank.bank.service.AccountService;
import com.bank.bank.service.TransactionService;
import com.bank.bank.service.TransferService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/bot")
@RequiredArgsConstructor
public class BotController {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final TransferService transferService;
    private final TransactionService transactionService;

    @Value("${telegram.bot.api-secret:dev-secret-key-123}")
    private String expectedSecret;

    private Long resolveUserId(HttpServletRequest req) {
        String secret = req.getHeader("X-Bot-Secret");
        String telegramIdHeader = req.getHeader("X-Telegram-Id");

        if (!expectedSecret.equals(secret)) {
            throw new SecurityException("Invalid bot secret");
        }

        if (telegramIdHeader == null) {
            return null;
        }

        Long telegramId = Long.parseLong(telegramIdHeader);
        return userRepository.findByTelegramId(telegramId)
                .map(User::getId)
                .orElse(null);
    }

    @GetMapping("/balance")
    public ResponseEntity<Map<String, Object>> getBalance(HttpServletRequest req) {
        Long userId = resolveUserId(req);
        if (userId == null) {
            return ResponseEntity.ok(Map.of(
                    "error", "❌ Сначала привяжите аккаунт командой /link <username>"
            ));
        }
        try {
            AccountResponse acc = accountService.getBalanceByUserId(userId);
            return ResponseEntity.ok(Map.of("balance", acc.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Ошибка получения баланса"));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getHistory(HttpServletRequest req) {
        Long userId = resolveUserId(req);
        if (userId == null) {
            return ResponseEntity.ok(Map.of(
                    "error", "❌ Сначала привяжите аккаунт командой /link <username>"
            ));
        }
        try {
            long count = transactionService.countByUserId(userId);
            return ResponseEntity.ok(Map.of("transactions", count + " операций"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("error", "Ошибка получения истории"));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<Map<String, String>> executeBotTransfer(
            HttpServletRequest req,
            @RequestBody TransferRequest transferReq) {

        Long userId = resolveUserId(req);
        if (userId == null) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "❌ Сначала привяжите аккаунт командой /link <username>"
            ));
        }

        try {
            Long fromAccountId = accountService.getAccountIdByUserId(userId);
            var tx = transferService.executeTransfer(
                    fromAccountId,
                    transferReq.getToAccountId(),
                    transferReq.getAmount());
            return ResponseEntity.ok(Map.of(
                    "status", "SUCCESS",
                    "message", "✅ Перевод выполнен! ID: " + tx.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "❌ " + e.getMessage()
            ));
        }
    }
}
