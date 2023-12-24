package ru.alemakave.mfstock.utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class TelegramBotUtils {
    public static void startCommandReceived(TelegramLongPollingBot telegramLongPollingBot, Long chatId, String name) {
        String answer = "Привет " + name + ".\n Введи код сотрудника под которым работаешь (код можно узнать отсканировав бейдж сотрудника)";
        sendMessage(telegramLongPollingBot, chatId, answer);
    }

    public static void sendMessage(TelegramLongPollingBot telegramLongPollingBot, Long chatId, String textToSend){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setText(textToSend);
        try {
            telegramLongPollingBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
