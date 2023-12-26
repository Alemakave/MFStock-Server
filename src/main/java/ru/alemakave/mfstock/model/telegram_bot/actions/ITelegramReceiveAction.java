package ru.alemakave.mfstock.model.telegram_bot.actions;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ITelegramReceiveAction {
    void call(Update update);
}
