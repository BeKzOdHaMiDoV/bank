package com.bank.bank.service;


import com.bank.bank.model.dto.TransferRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class TelegramApiCaller {

    private final RestTemplate restTemplate = new RestTemplate();

    // ✅ Жёсткий порт по умолчанию (если @Value не сработает)
    @Value("${server.port:8080}")
    private int port = 8080;

    @Value("${telegram.bot.api-secret:dev-secret-key-123}")
    private String apiSecret;

    // ✅ Формируем URL в методе, а не в поле
    private String getBaseUrl() {
        return "http://localhost:" + port + "/api/bot";
    }

    private HttpHeaders headers(Long telegramId) {
        HttpHeaders h = new HttpHeaders();
        h.set("X-Bot-Secret", apiSecret);
        h.set("X-Telegram-Id", telegramId.toString());
        h.setContentType(MediaType.APPLICATION_JSON);
        return h;
    }

    public String getBalance(Long telegramId) {
        try {
            HttpEntity<Void> req = new HttpEntity<>(headers(telegramId));
            ResponseEntity<Map> res = restTemplate.exchange(
                    getBaseUrl() + "/balance", HttpMethod.GET, req, Map.class);
            Object balance = res.getBody().get("balance");
            return "💰 Ваш баланс: " + balance;
        } catch (Exception e) {
            log.warn("Ошибка баланса для TG {}: {}", telegramId, e.getMessage());
            return "❌ Ошибка получения баланса";
        }
    }

    public String getHistory(Long telegramId) {
        try {
            HttpEntity<Void> req = new HttpEntity<>(headers(telegramId));
            ResponseEntity<Map> res = restTemplate.exchange(
                    getBaseUrl() + "/history", HttpMethod.GET, req, Map.class);
            return "📜 История: " + res.getBody().get("transactions");
        } catch (Exception e) {
            log.warn("Ошибка истории для TG {}: {}", telegramId, e.getMessage());
            return "❌ Ошибка получения истории";
        }
    }

    public String executeTransfer(Long telegramId, BigDecimal amount, Long toAccountId) {
        try {
            HttpHeaders headers = headers(telegramId);
            TransferRequest reqBody = new TransferRequest();
            reqBody.setAmount(amount);
            reqBody.setToAccountId(toAccountId);

            HttpEntity<TransferRequest> entity = new HttpEntity<>(reqBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    getBaseUrl() + "/transfer", entity, Map.class);

            Map body = response.getBody();
            if ("SUCCESS".equals(body.get("status"))) {
                return "✅ " + body.get("message");
            }
            return "❌ " + body.get("message");
        } catch (Exception e) {
            log.warn("Ошибка перевода для TG {}: {}", telegramId, e.getMessage());
            return "⚠️ Ошибка соединения с сервером";
        }
    }
}