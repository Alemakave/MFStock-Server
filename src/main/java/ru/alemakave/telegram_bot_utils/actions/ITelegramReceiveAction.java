package ru.alemakave.telegram_bot_utils.actions;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ITelegramReceiveAction {
    void call(Update update);
    void action(Update update);
}
