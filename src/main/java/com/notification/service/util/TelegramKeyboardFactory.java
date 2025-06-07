package com.notification.service.util;

import com.notification.service.model.NotificationType;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Component
public class TelegramKeyboardFactory {

    public static InlineKeyboardMarkup createSingleButtonKeyboard(String buttonText, NotificationType type, Long taskId) {
        InlineKeyboardButton button = new InlineKeyboardButton(buttonText);
        button.setCallbackData(type + ":" + taskId);
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }

    public static ReplyKeyboardMarkup createMainMenuKeyboard() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        row1.add(TelegramButtonLabels.CREATE_MR);
        row1.add(TelegramButtonLabels.CREATE_THRESHOLD);

        KeyboardRow row2 = new KeyboardRow();
        row2.add(TelegramButtonLabels.NOTIFY_THRESHOLD_CHANGE);
        row2.add(TelegramButtonLabels.MERGE_MR);

        KeyboardRow row3 = new KeyboardRow();
        row3.add(TelegramButtonLabels.CANCEL_ACTION);

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);

        replyKeyboardMarkup.setKeyboard(keyboard);
        replyKeyboardMarkup.setResizeKeyboard(true);
        return replyKeyboardMarkup;
    }

    public static <T> InlineKeyboardMarkup createSingleColumnKeyboard(List<T> items,
                                                               Function<T, String> textMapper,
                                                               Function<T, String> callbackMapper) {
        List<List<InlineKeyboardButton>> buttons = items.stream()
                .map(item -> {
                    InlineKeyboardButton button = new InlineKeyboardButton();
                    button.setText(textMapper.apply(item));
                    button.setCallbackData(callbackMapper.apply(item));
                    return List.of(button);
                })
                .toList();

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(buttons);
        return inlineKeyboardMarkup;
    }
}
