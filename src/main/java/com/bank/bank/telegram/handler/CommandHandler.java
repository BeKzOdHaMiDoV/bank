package com.bank.bank.telegram.handler;




import com.bank.bank.repository.UserRepository;
import com.bank.bank.service.TelegramApiCaller;
import com.bank.bank.telegram.TelegramBot;
import com.bank.bank.telegram.handler.model.TransferContext;
import com.bank.bank.telegram.handler.model.TransferState;
import com.bank.bank.telegram.keyboard.KeyboardFactory;
import com.bank.bank.telegram.service.UserStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
public class CommandHandler {

    @Autowired @Lazy
    private TelegramBot bot;

    private final TelegramApiCaller apiCaller;
    private final UserStateService stateService;
    private final UserRepository userRepository;

    public CommandHandler(TelegramApiCaller apiCaller, UserStateService stateService, UserRepository userRepository) {
        this.apiCaller = apiCaller;
        this.stateService = stateService;
        this.userRepository = userRepository;
    }

    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long telegramId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText().trim();

        if ("/cancel".equals(text) || "/start".equals(text)) {
            stateService.reset(telegramId);
            showMainMenu(chatId);
            return;
        }

        TransferContext ctx = stateService.getContext(telegramId);

        switch (ctx.getState()) {
            case WAIT_AMOUNT -> handleWaitAmount(chatId, telegramId, text);
            case WAIT_RECEIVER -> handleWaitReceiver(chatId, telegramId, text);
            case WAIT_CONFIRM -> handleWaitConfirm(chatId, telegramId, text);
            default -> handleNewCommand(chatId, telegramId, text);
        }
    }

    public void handleCallback(Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        Long telegramId = update.getCallbackQuery().getFrom().getId();
        String callbackData = update.getCallbackQuery().getData();

        switch (callbackData) {
            case "balance" -> bot.sendText(chatId, apiCaller.getBalance(telegramId));
            case "transfer" -> startTransferFlow(chatId, telegramId);
            case "history" -> bot.sendText(chatId, apiCaller.getHistory(telegramId));
            case "link_account" -> bot.sendText(chatId, "🔗 Отправьте команду /link <username>");
            case "back_to_main" -> {
                stateService.reset(telegramId);
                showMainMenu(chatId);
            }
            case "confirm_yes" -> handleConfirmYes(chatId, telegramId);
            case "confirm_no" -> {
                bot.sendText(chatId, "❌ Перевод отменён");
                stateService.reset(telegramId);
                showMainMenu(chatId);
            }
        }
    }

    private void showMainMenu(Long chatId) {
        bot.sendTextWithKeyboard(chatId, "👋 Добро пожаловать в BankBot!\nВыберите действие:",
                KeyboardFactory.getMainMenuKeyboard());
    }

    private void handleNewCommand(Long chatId, Long telegramId, String text) {
        if (text.startsWith("/link ")) {
            handleLink(chatId, telegramId, text.substring(6).trim());
            return;
        }
        showMainMenu(chatId);
    }

    private void handleLink(Long chatId, Long telegramId, String username) {
        userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    user.setTelegramId(telegramId);
                    userRepository.save(user);
                    bot.sendText(chatId, "✅ Аккаунт @" + username + " привязан!");
                    showMainMenu(chatId);
                },
                () -> bot.sendText(chatId, "❌ Пользователь не найден")
        );
    }

    private void startTransferFlow(Long chatId, Long telegramId) {
        TransferContext ctx = stateService.getContext(telegramId);
        ctx.setState(TransferState.WAIT_AMOUNT);
        bot.sendTextWithKeyboard(chatId, "💸 Введите сумму перевода:",
                KeyboardFactory.getTransferMenuKeyboard());
    }

    private void handleWaitAmount(Long chatId, Long telegramId, String text) {
        try {
            BigDecimal amount = new BigDecimal(text);
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                bot.sendText(chatId, "⚠️ Сумма должна быть > 0");
                return;
            }
            TransferContext ctx = stateService.getContext(telegramId);
            ctx.setAmount(amount);
            ctx.setState(TransferState.WAIT_RECEIVER);
            bot.sendText(chatId, "👤 Введите ID получателя:");
        } catch (NumberFormatException e) {
            bot.sendText(chatId, "❌ Введите число (например: 50.00)");
        }
    }

    private void handleWaitReceiver(Long chatId, Long telegramId, String text) {
        try {
            Long toId = Long.parseLong(text);
            TransferContext ctx = stateService.getContext(telegramId);
            ctx.setToAccountId(toId);
            ctx.setState(TransferState.WAIT_CONFIRM);

            String summary = String.format("📋 Подтвердите перевод:\n💰 Сумма: %s\n👤 ID: %s",
                    ctx.getAmount(), toId);
            bot.sendTextWithKeyboard(chatId, summary, KeyboardFactory.getConfirmTransferKeyboard());
        } catch (NumberFormatException e) {
            bot.sendText(chatId, "❌ ID должен быть числом");
        }
    }

    private void handleWaitConfirm(Long chatId, Long telegramId, String text) {
        // Не используется - обработка через callback
    }

    private void handleConfirmYes(Long chatId, Long telegramId) {
        TransferContext ctx = stateService.getContext(telegramId);
        String result = apiCaller.executeTransfer(telegramId, ctx.getAmount(), ctx.getToAccountId());
        bot.sendText(chatId, result);
        stateService.reset(telegramId);
        showMainMenu(chatId);
    }
}