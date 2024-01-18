package ru.alemakave.telegram_bot_utils.actions;

import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.telegram_bot.TelegramBot;
import ru.alemakave.mfstock.telegram_bot.TelegramCachePhotoFilesManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TelegramReceivePhotoAction implements ITelegramReceiveAction {
    public final TelegramBot bot;
    public final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;

    public TelegramReceivePhotoAction(TelegramBot bot) {
        this.bot = bot;
        this.telegramCachePhotoFilesManager = bot.getPhotoCache();
    }

    @Override
    public void call(Update update) {
        if (hasPhoto(update)) {
            action(update);
        }
    }

    public boolean hasPhoto(Update update) {
        return update.getMessage().hasPhoto();
    }

    public List<PhotoSize> getPhoto(Update update) {
        List<PhotoSize> photos = update.getMessage().getPhoto();

        return new ArrayList<>(Collections.singleton(photos.get(photos.size() - 1)));
    }
}
