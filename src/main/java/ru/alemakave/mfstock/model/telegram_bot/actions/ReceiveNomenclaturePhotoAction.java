package ru.alemakave.mfstock.model.telegram_bot.actions;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.model.telegram_bot.TelegramCachePhotoFilesManager;
import ru.alemakave.mfstock.service.TelegramBot;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceivePhotoAction;

import java.util.List;

@Component
public class ReceiveNomenclaturePhotoAction extends TelegramReceivePhotoAction {
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    private final TelegramBot bot;

    public ReceiveNomenclaturePhotoAction(TelegramCachePhotoFilesManager telegramCachePhotoFilesManager, TelegramBot bot) {
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
        this.bot = bot;
    }

    @Override
    public void call(Update update) {
        List<PhotoSize> photoDataList = update.getMessage().getPhoto();
        PhotoSize photoData = photoDataList.get(photoDataList.size() - 1);
        GetFile getFile = new GetFile(photoData.getFileId());
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            telegramCachePhotoFilesManager.downloadPhotoFile(bot, file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
