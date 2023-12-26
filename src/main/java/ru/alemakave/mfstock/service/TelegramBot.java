package ru.alemakave.mfstock.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.configs.model.TelegramBotConfigs;
import ru.alemakave.mfstock.model.telegram_bot.TGUserJson;
import ru.alemakave.mfstock.model.telegram_bot.TelegramCachePhotoFilesManager;
import ru.alemakave.mfstock.model.telegram_bot.UserManager;
import ru.alemakave.mfstock.model.telegram_bot.actions.ITelegramReceiveAction;
import ru.alemakave.mfstock.model.telegram_bot.actions.ReceiveNomenclaturePhotoAction;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.alemakave.mfstock.utils.TelegramBotUtils.*;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfigs botConfigs;
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    private final UserManager userManager;
    private final ArrayList<ITelegramReceiveAction> actions = new ArrayList<>();

    public TelegramBot(TelegramBotConfigs botConfigs, TelegramCachePhotoFilesManager telegramCachePhotoFilesManager, UserManager userManager) {
        super(botConfigs.getBotToken());
        this.botConfigs = botConfigs;
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
        this.userManager = userManager;
        initActions();
    }

    @Override
    public String getBotUsername() {
        return botConfigs.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Работа бота тутъ
        if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            if (userManager.isRegisteredUserByChatId(chatId)) {
                onRegisteredUserReceivedMessage(update);
            } else {
                onNotRegisteredUserReceivedMessage(update);
            }
        }
    }

    private void onRegisteredUserReceivedMessage(Update update) {
        long chatId = update.getMessage().getChatId();

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.equalsIgnoreCase("/get-photos")) {
                for (File image : telegramCachePhotoFilesManager.getTempNomPhotoFiles()) {
                    sendImageMessage(this, chatId, image);
                }
            }
        }

        for (ITelegramReceiveAction action : actions) {
            if (update.getMessage().hasPhoto()) {
                if (action.getClass().equals(ReceiveNomenclaturePhotoAction.class)) {
                    action.call(update);
                }
            }
        }
    }

    private void onNotRegisteredUserReceivedMessage(Update update) {
        long chatId = update.getMessage().getChatId();

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.equalsIgnoreCase("/start")) {
                startCommandReceived(this, chatId, update.getMessage().getChat().getFirstName());
            } else {
                if (messageText.startsWith("E-")) {
                    messageText = messageText.substring(2);
                }
                final Pattern pattern = Pattern.compile("[0-9]{4}", Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(messageText);
                if (matcher.find()) {
                    userManager.registryUser(new TGUserJson(chatId, Integer.parseInt(messageText)));
                    sendMessage(this, chatId, "Вы зарегистрированы!");
                } else {
                    sendUnregisteredMessage(this, chatId);
                }
            }
        } else {
            sendUnregisteredMessage(this, chatId);
        }
    }

    private void initActions() {
        actions.add(new ReceiveNomenclaturePhotoAction(telegramCachePhotoFilesManager, this));
    }
}
