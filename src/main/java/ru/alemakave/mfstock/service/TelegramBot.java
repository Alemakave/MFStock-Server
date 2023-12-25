package ru.alemakave.mfstock.service;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.configs.model.TelegramBotConfigs;

import static ru.alemakave.mfstock.utils.TelegramBotUtils.startCommandReceived;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfigs botConfigs;

    public TelegramBot(TelegramBotConfigs botConfigs) {
        super(botConfigs.getBotToken());
        this.botConfigs = botConfigs;
    }

    @Override
    public String getBotUsername() {
        return botConfigs.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Работа бота тутъ
        if(update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            switch (messageText) {
                case "/start":
                    startCommandReceived(this, chatId, update.getMessage().getChat().getFirstName());
                    break;
            }
        }
    }
}
