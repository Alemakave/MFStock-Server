package ru.alemakave.mfstock.telegram_bot.actions;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.telegram_bot.TelegramBot;
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
    /*
    Если фото было передано без какой-либо информации, добавлять с названием unknown_{index}.
    Добавить кнопку с получением фото по коду.
    Добавить кнопку с выводом кнопок с именами выше, при нажатии отправлять сообщение на получение фото с данным именем.
    Добавить переименование файла в формате unknown_{index} = {новое название}
     */
}
