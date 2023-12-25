package ru.alemakave.mfstock.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.configs.model.TelegramBotConfigs;
import ru.alemakave.mfstock.model.telegram_bot.TelegramCachePhotoFilesManager;

import java.util.List;

import static ru.alemakave.mfstock.utils.TelegramBotUtils.startCommandReceived;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfigs botConfigs;
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;

    public TelegramBot(TelegramBotConfigs botConfigs, TelegramCachePhotoFilesManager telegramCachePhotoFilesManager) {
        super(botConfigs.getBotToken());
        this.botConfigs = botConfigs;
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
    }

    @Override
    public String getBotUsername() {
        return botConfigs.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Работа бота тутъ
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                String messageText = update.getMessage().getText();
                long chatId = update.getMessage().getChatId();

                if (messageText.equalsIgnoreCase("/start")) {
                    startCommandReceived(this, chatId, update.getMessage().getChat().getFirstName());
                }
            }
            if (update.getMessage().hasPhoto()) {
                List<PhotoSize> photoDataList = update.getMessage().getPhoto();
                for (PhotoSize photoData : photoDataList) {
                    GetFile getFile = new GetFile(photoData.getFileId());
                    try {
                        org.telegram.telegrambots.meta.api.objects.File file = execute(getFile);
                        telegramCachePhotoFilesManager.downloadPhotoFile(this, file);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
