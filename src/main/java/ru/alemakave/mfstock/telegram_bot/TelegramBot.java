package ru.alemakave.mfstock.telegram_bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.alemakave.mfstock.model.configs.TelegramBotConfigs;
import ru.alemakave.mfstock.service.DBServiceImpl;
import ru.alemakave.mfstock.telegram_bot.actions.GetPhotoTelegramAction;
import ru.alemakave.mfstock.telegram_bot.actions.ReceiveNomenclaturePhotoAction;
import ru.alemakave.mfstock.telegram_bot.actions.RegistrationMessageAction;
import ru.alemakave.mfstock.telegram_bot.exception.DownloadFileException;
import ru.alemakave.slib.file.CachedFile;
import ru.alemakave.telegram_bot_utils.actions.ITelegramReceiveAction;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceiveMessageAction;
import ru.alemakave.telegram_bot_utils.actions.TelegramReceivePhotoAction;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final TelegramBotConfigs botConfigs;
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;
    private final UserManager userManager;
    private final Map<String, List<ITelegramReceiveAction>> actions = new HashMap<>();
    private final DBServiceImpl dbService;

    public TelegramBot(TelegramBotConfigs botConfigs, TelegramCachePhotoFilesManager telegramCachePhotoFilesManager, UserManager userManager, DBServiceImpl dbService) {
        super(botConfigs.getBotToken());
        this.botConfigs = botConfigs;
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
        this.userManager = userManager;
        this.dbService = dbService;

        actions.put(TelegramReceiveMessageAction.class.getName(), new ArrayList<>());
        actions.put(TelegramReceivePhotoAction.class.getName(), new ArrayList<>());

        initActions();
    }

    @Override
    public String getBotUsername() {
        return botConfigs.getBotName();
    }

    @Override
    public void onUpdateReceived(Update update) {
        // Работа бота тутъ
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            AnswerCallbackQuery answerCallback = new AnswerCallbackQuery();
            answerCallback.setCallbackQueryId(callbackQuery.getId());
            callbackQuery.getMessage().setReplyMarkup(null);

            try {
                execute(answerCallback);
                String[] data = callbackQuery.getData().split("->");
                String fileAbsolutePath = data[0];
                String nomCode = data[1];

                EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(callbackQuery.getMessage().getChatId());
                editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
                telegramCachePhotoFilesManager.moveFromTemp(new File(fileAbsolutePath), nomCode);
                editMessageText.setText("Прикреплено к " + nomCode);

                execute(editMessageText);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        if (update.hasMessage()) {
            if (update.getMessage().hasPhoto()) {
                actions.get(TelegramReceivePhotoAction.class.getName()).forEach(action -> action.call(update));
            } else if (update.getMessage().hasText()) {
                actions.get(TelegramReceiveMessageAction.class.getName()).forEach(action -> action.call(update));
            }
        }
    }

    private void initActions() {
        addAction(new ReceiveNomenclaturePhotoAction(this, dbService, userManager));
        addAction(new RegistrationMessageAction(this, userManager));
        addAction(new GetPhotoTelegramAction(this));
    }

    public void addAction(ITelegramReceiveAction action) {
        if (action instanceof TelegramReceiveMessageAction) {
            actions.get(TelegramReceiveMessageAction.class.getName()).add(action);
        } else if (action instanceof TelegramReceivePhotoAction) {
            actions.get(TelegramReceivePhotoAction.class.getName()).add(action);
        }
    }

    public TelegramCachePhotoFilesManager getPhotoCache() {
        return telegramCachePhotoFilesManager;
    }

    public CachedFile downloadPhoto(org.telegram.telegrambots.meta.api.objects.File file) throws TelegramApiException, DownloadFileException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        int photoNumber = 1;
        String tempDirPath = System.getenv("TEMP");
        String photoFileName = String.format("%s_%d_tmp.jpg", dateTimeFormatter.format(now), photoNumber);

        while (Files.exists(Path.of(tempDirPath, photoFileName))) {
            photoNumber++;
            photoFileName = String.format("%s_%d_tmp.jpg", dateTimeFormatter.format(now), photoNumber);
        }

        File tempFile = new File(tempDirPath, photoFileName);

        downloadFile(file, tempFile);

        if (!tempFile.exists()) {
            throw new DownloadFileException("File not downloaded");
        }

        return new CachedFile(tempFile);
    }
}
