package ru.alemakave.mfstock.model.telegram_bot.actions;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.service.telegram_bot.TelegramBot;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceiveMessageAction;

import java.io.File;

import static ru.alemakave.mfstock.utils.TelegramBotUtils.sendImageMessage;

public class GetPhotoTelegramAction extends TelegramReceiveMessageAction {
    public GetPhotoTelegramAction(TelegramBot bot) {
        super(bot, "/get-photos");
    }

    @Override
    public void action(Update update) {
        long chatId = update.getMessage().getChatId();

        for (File image : telegramCachePhotoFilesManager.getTempNomPhotoFiles()) {
            sendImageMessage(bot, chatId, image);
        }
    }
}
