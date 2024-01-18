package ru.alemakave.telegram_bot_utils.actions;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.telegram_bot.TelegramBot;
import ru.alemakave.mfstock.telegram_bot.TelegramCachePhotoFilesManager;

public abstract class TelegramReceiveMessageAction implements ITelegramReceiveAction {
    public final TelegramBot bot;
    public final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    public final String message;

    public TelegramReceiveMessageAction(TelegramBot bot, String message) {
        this.bot = bot;
        this.message = message;
        telegramCachePhotoFilesManager = bot.getPhotoCache();
    }

    public void call(Update update) {
        if (hasText(update) && getMessage(update).equalsIgnoreCase(message)) {
            action(update);
        }
    }

    public boolean hasText(Update update) {
        return update.getMessage().hasText();
    }

    public String getMessage(Update update) {
        return update.getMessage().getText();
    }
}
