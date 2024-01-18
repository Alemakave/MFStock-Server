package ru.alemakave.mfstock.telegram_bot.actions;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.model.telegram_bot.TGUserJson;
import ru.alemakave.mfstock.telegram_bot.TelegramBot;
import ru.alemakave.mfstock.telegram_bot.TelegramBotReceiveMode;
import ru.alemakave.mfstock.telegram_bot.UserManager;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceiveMessageAction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.alemakave.mfstock.utils.TelegramBotUtils.*;

@Component
public class RegistrationMessageAction extends TelegramReceiveMessageAction {
    private final UserManager userManager;

    public RegistrationMessageAction(TelegramBot bot, UserManager userManager) {
        super(bot, "/start");
        this.userManager = userManager;
    }

    @Override
    public void action(Update update) {}

    @Override
    public void call(Update update) {
        long chatId = update.getMessage().getChatId();

        if (!userManager.isRegisteredUserByChatId(chatId)) {
            onNotRegisteredUserReceivedMessage(update);
        }
    }

    private void onNotRegisteredUserReceivedMessage(Update update) {
        long chatId = update.getMessage().getChatId();

        if (update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.equalsIgnoreCase("/start")) {
                startCommandReceived(bot, chatId, update.getMessage().getChat().getFirstName());
            } else {
                if (messageText.startsWith("E-")) {
                    messageText = messageText.substring(2);
                }
                final Pattern pattern = Pattern.compile("[0-9]{4}", Pattern.MULTILINE);
                final Matcher matcher = pattern.matcher(messageText);
                if (matcher.find()) {
                    userManager.registryUser(new TGUserJson(chatId, Integer.parseInt(messageText), TelegramBotReceiveMode.NONE));
                    sendMessage(bot, chatId, "Вы зарегистрированы!");
                } else {
                    sendUnregisteredMessage(bot, chatId);
                }
            }
        } else {
            sendUnregisteredMessage(bot, chatId);
        }
    }
}
