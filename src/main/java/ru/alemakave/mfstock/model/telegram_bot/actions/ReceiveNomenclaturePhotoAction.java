package ru.alemakave.mfstock.model.telegram_bot.actions;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.alemakave.mfstock.model.table.Table;
import ru.alemakave.mfstock.model.table.TableCell;
import ru.alemakave.mfstock.model.table.TableRow;
import ru.alemakave.mfstock.model.telegram_bot.TelegramCachePhotoFilesManager;
import ru.alemakave.mfstock.model.telegram_bot.UserManager;
import ru.alemakave.mfstock.service.DBServiceImpl;
import ru.alemakave.mfstock.service.telegram_bot.TelegramBot;
import ru.alemakave.mfstock.service.telegram_bot.TelegramBotReceiveMode;
import ru.alemakave.mfstock.utils.TelegramBotUtils;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceivePhotoAction;

import java.io.File;
import java.util.List;

@Component
public class ReceiveNomenclaturePhotoAction extends TelegramReceivePhotoAction {
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    private final TelegramBot bot;
    private final DBServiceImpl dbService;
    private final UserManager userManager;

    public ReceiveNomenclaturePhotoAction(TelegramCachePhotoFilesManager telegramCachePhotoFilesManager,
                                          TelegramBot bot,
                                          DBServiceImpl dbService,
                                          UserManager userManager) {
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
        this.bot = bot;
        this.dbService = dbService;
        this.userManager = userManager;
    }

    @Override
    public void call(Update update) {
        List<PhotoSize> photoDataList = update.getMessage().getPhoto();
        PhotoSize photoData = photoDataList.get(photoDataList.size() - 1);
        GetFile getFile = new GetFile(photoData.getFileId());
        Long chatId = update.getMessage().getChatId();
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile);
            File tmpFile = telegramCachePhotoFilesManager.downloadPhotoFile(bot, file);

            if (update.getMessage().getCaption() != null) {
                String foundedData = dbService.findFromScan(update.getMessage().getCaption());
                ObjectMapper mapper = new ObjectMapper();
                List<TableRow> rows = mapper.readValue(foundedData, Table.class).getRows();

                if (rows.size() > 1) {
                    List<TableCell> headerRow = rows.get(0).getCells();
                    int nomCodeIndex = headerRow.indexOf(new TableCell("Номенклатурный код"));
                    int nomNameIndex = headerRow.indexOf(new TableCell("Наименование"));
                    telegramCachePhotoFilesManager.moveFromTemp(tmpFile, rows.get(1).getCells().get(nomCodeIndex).getValue());
                    TelegramBotUtils.sendMessage(bot, chatId, "Приложено фото к\n" + rows.get(1).getCells().get(nomNameIndex).getValue());
                } else {
                    TelegramBotUtils.sendMessage(bot, chatId, "Оборудование по следующей информации не найдено\n" + update.getMessage().getCaption());
                    TelegramBotUtils.sendMessage(bot, chatId, "Введите код товара, либо любую другую информацию оборудования");
                    userManager.setUserModeByChatId(chatId, TelegramBotReceiveMode.WAIT_NOM_DATA);
                }
            } else {
                TelegramBotUtils.sendMessage(bot, chatId, "Введите код товара, либо любую другую информацию оборудования");
                userManager.setUserModeByChatId(chatId, TelegramBotReceiveMode.WAIT_NOM_DATA);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
