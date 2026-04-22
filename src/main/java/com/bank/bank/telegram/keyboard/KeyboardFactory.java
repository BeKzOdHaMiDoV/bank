package com.bank.bank.telegram.keyboard;


import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    // Главное меню
    public static InlineKeyboardMarkup getMainMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createRow("💰 Баланс", "balance"));
        rows.add(createRow("💸 Перевод", "transfer"));
        rows.add(createRow("📜 История", "history"));
        rows.add(createRow("🔗 Привязать аккаунт", "link_account"));

        markup.setKeyboard(rows);
        return markup;
    }

    // Меню перевода
    public static InlineKeyboardMarkup getTransferMenuKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createRow("🔙 Назад", "back_to_main"));

        markup.setKeyboard(rows);
        return markup;
    }

    // Подтверждение перевода
    public static InlineKeyboardMarkup getConfirmTransferKeyboard() {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();

        rows.add(createRow("✅ Подтвердить", "confirm_yes"));
        rows.add(createRow("❌ Отмена", "confirm_no"));

        markup.setKeyboard(rows);
        return markup;
    }

    private static List<InlineKeyboardButton> createRow(String text, String callbackData) {
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        row.add(button);
        return row;
    }
}