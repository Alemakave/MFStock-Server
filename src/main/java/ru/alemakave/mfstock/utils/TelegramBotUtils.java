package ru.alemakave.mfstock.utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TelegramBotUtils {
    public static void startCommandReceived(TelegramLongPollingBot telegramLongPollingBot, Long chatId, String name) {
        String answer = "Привет " + name + ".\n Введи код сотрудника под которым работаешь (код можно узнать отсканировав бейдж сотрудника)";
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(answer);

        message.setReplyMarkup(null);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        row1.add("/get-photos");
        row1.add("Row 1 Button 2");
        row1.add("Row 1 Button 3");
        keyboard.add(row1);
        KeyboardRow row2 = new KeyboardRow();
        row2.add("Row 2 Button 1");
        row2.add("Row 2 Button 2");
        row2.add("Row 2 Button 3");
        keyboard.add(row2);
        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {
            telegramLongPollingBot.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendImageMessage(TelegramLongPollingBot telegramLongPollingBot, Long chatId, File image) {
        SendPhoto photoMessage = new SendPhoto();
        photoMessage.setChatId(chatId);
        photoMessage.setPhoto(new InputFile(image));
        photoMessage.setCaption(image.getName());

        try {
            telegramLongPollingBot.execute(photoMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
