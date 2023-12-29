package ru.alemakave.mfstock.service.telegram_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.alemakave.mfstock.configs.model.TelegramBotConfigs;

@Component
public class BotInitializer {
    private final TelegramBot telegramBot;
    private final TelegramBotConfigs botConfigs;
    private final Logger logger = LoggerFactory.getLogger(BotInitializer.class);

    @Autowired
    public BotInitializer(TelegramBot telegramBot, TelegramBotConfigs botConfigs) {
        this.telegramBot = telegramBot;
        this.botConfigs = botConfigs;
    }

    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        try{
            if (botConfigs.getBotName() == null || botConfigs.getBotToken() == null) {
                if (botConfigs.getBotName() == null) {
                    logger.error("Telegram bot name is null.");
                }

                if (botConfigs.getBotToken() == null) {
                    logger.error("Telegram bot token is null.");
                }

                return;
            }
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
