package ru.alemakave.mfstock.model.telegram_bot.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alemakave.mfstock.model.table.Table;
import ru.alemakave.mfstock.model.table.TableCell;
import ru.alemakave.mfstock.model.table.TableRow;
import ru.alemakave.mfstock.model.telegram_bot.UserManager;
import ru.alemakave.mfstock.service.DBServiceImpl;
import ru.alemakave.mfstock.service.telegram_bot.TelegramBot;
import ru.alemakave.mfstock.service.telegram_bot.TelegramBotReceiveMode;
import ru.alemakave.mfstock.utils.TelegramBotUtils;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceivePhotoAction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ReceiveNomenclaturePhotoAction extends TelegramReceivePhotoAction {
    private final DBServiceImpl dbService;
    private final UserManager userManager;
    private final int MAX_SELECT_NOM_COUNT = 10;
    private String groupId = null;
    private String groupCaption = null;

    public ReceiveNomenclaturePhotoAction(TelegramBot bot,
                                          DBServiceImpl dbService,
                                          UserManager userManager) {
        super(bot);
        this.dbService = dbService;
        this.userManager = userManager;
    }

    @Override
    public void action(Update update) {
        List<PhotoSize> photoDataList = getPhoto(update);
        PhotoSize photoData = photoDataList.get(photoDataList.size() - 1);
        GetFile getFile = new GetFile(photoData.getFileId());
        Long chatId = update.getMessage().getChatId();
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            File tmpFile = telegramCachePhotoFilesManager.downloadPhotoFile(bot, file);

            boolean sendStatusMassage = false;

            String caption = update.getMessage().getCaption();
            String mediaGroupId = update.getMessage().getMediaGroupId();

            if (caption == null && mediaGroupId.equals(groupId)) {
                caption = groupCaption;
            }

            if (mediaGroupId != null && groupCaption == null) {
                groupId = mediaGroupId;
                groupCaption = caption;
                sendStatusMassage = true;
            } else if (mediaGroupId == null) {
                groupId = null;
                groupCaption = null;
                sendStatusMassage = true;
            }


            if (caption != null || (groupId != null && groupCaption != null)) {
                String foundedData = dbService.findFromScan(caption);
                ObjectMapper mapper = new ObjectMapper();
                List<TableRow> rows = mapper.readValue(foundedData, Table.class).getRows();
                List<TableCell> headerRow = rows.get(0).getCells();
                int nomCodeIndex = headerRow.indexOf(new TableCell("Номенклатурный код"));
                int nomNameIndex = headerRow.indexOf(new TableCell("Наименование"));

                int finalNomCodeIndex = nomCodeIndex;
                rows = rows
                        .stream()
                        .parallel()
                        .map(tableRow -> new TableRow(Arrays.asList(tableRow.getCells().get(finalNomCodeIndex), tableRow.getCells().get(nomNameIndex))))
                        .distinct()
                        .collect(Collectors.toList());

                if (rows.size() > 1) {
                    if (rows.size() > 2) {
                        callSelectNom(update, rows, tmpFile);
                    } else {
                        headerRow = rows.get(0).getCells();
                        nomCodeIndex = headerRow.indexOf(new TableCell("Номенклатурный код"));

                        telegramCachePhotoFilesManager.moveFromTemp(tmpFile, rows.get(1).getCells().get(nomCodeIndex).getValue());
                        if (sendStatusMassage) {
                            TelegramBotUtils.sendMessage(bot, chatId, "Приложено фото к\n" + rows.get(1).toString().replace('|', ' '));
                        }
                    }
                } else {
                    if (sendStatusMassage) {
                        TelegramBotUtils.sendMessage(bot, chatId, "Оборудование по следующей информации не найдено\n" + update.getMessage().getCaption());
                        TelegramBotUtils.sendMessage(bot, chatId, "Введите код товара, либо любую другую информацию оборудования");
                    }
                    userManager.setUserModeByChatId(chatId, TelegramBotReceiveMode.WAIT_NOM_DATA);
                }
            } else {
                if (sendStatusMassage) {
                    TelegramBotUtils.sendMessage(bot, chatId, "Введите код товара, либо любую другую информацию оборудования");
                }
                userManager.setUserModeByChatId(chatId, TelegramBotReceiveMode.WAIT_NOM_DATA);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void callSelectNom(Update update, List<TableRow> rows, File file) {
        Long chatId = update.getMessage().getChatId();
        List<TableCell> headerRow = rows.get(0).getCells();
        int nomCodeIndex = headerRow.indexOf(new TableCell("Номенклатурный код"));
        int nomNameIndex = headerRow.indexOf(new TableCell("Наименование"));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        for (int i = 1; i < Math.min(rows.size(), MAX_SELECT_NOM_COUNT + 1); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(rows.get(i).getCells().get(nomNameIndex).getValue());
            String callbackMessage = file.getAbsolutePath() + "->" + rows.get(i).getCells().get(nomCodeIndex).getValue();
            button.setCallbackData(callbackMessage);

            List<InlineKeyboardButton> buttonsRow = new ArrayList<>();
            buttonsRow.add(button);

            buttons.add(buttonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(buttons);


        String message = "Выберите оборудование к которому прикрепить фото";

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);

        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
