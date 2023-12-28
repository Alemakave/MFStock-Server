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
import ru.alemakave.mfstock.service.DBServiceImpl;
import ru.alemakave.mfstock.service.TelegramBot;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceivePhotoAction;

import java.io.File;
import java.util.List;

@Component
public class ReceiveNomenclaturePhotoAction extends TelegramReceivePhotoAction {
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    private final TelegramBot bot;
    private final DBServiceImpl dbService;

    public ReceiveNomenclaturePhotoAction(TelegramCachePhotoFilesManager telegramCachePhotoFilesManager, TelegramBot bot, DBServiceImpl dbService) {
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
        this.bot = bot;
        this.dbService = dbService;
    }

    @Override
    public void call(Update update) {
        List<PhotoSize> photoDataList = update.getMessage().getPhoto();
        PhotoSize photoData = photoDataList.get(photoDataList.size() - 1);
        GetFile getFile = new GetFile(photoData.getFileId());
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
                    telegramCachePhotoFilesManager.moveFromTemp(tmpFile, rows.get(1).getCells().get(nomCodeIndex).getValue());
                }
                System.out.println(rows);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
