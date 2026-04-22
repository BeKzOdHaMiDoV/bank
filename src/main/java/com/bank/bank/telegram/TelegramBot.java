package com.bank.bank.telegram;

import com.bank.bank.telegram.handler.CommandHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramBot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;

    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                commandHandler.handle(update);
            }
        } else if (update.hasCallbackQuery()) {
            // Обработка нажатий на кнопки
            commandHandler.handleCallback(update);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    public void sendText(Long chatId, String text) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки: {}", e.getMessage());
        }
    }

    public void sendTextWithKeyboard(Long chatId, String text, InlineKeyboardMarkup keyboard) {
        SendMessage msg = new SendMessage(chatId.toString(), text);
        msg.setReplyMarkup(keyboard);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            log.error("❌ Ошибка отправки с клавиатурой: {}", e.getMessage());
        }
    }
}