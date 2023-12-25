package ru.alemakave.mfstock.model.telegram_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class TelegramCachePhotoFilesManager {
    private final Map<String, File> tempFiles = new TreeMap<>();

    @Value("${mfstock.photo.cache.path:.}")
    private String photoCacheDir;

    public void addTempPhotoFile(File file) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        tempFiles.put(dateTimeFormatter.format(now), file);
    }

    public void downloadPhotoFile(DefaultAbsSender bot, org.telegram.telegrambots.meta.api.objects.File file) throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();
        File tempFile = new File(photoCacheDir, dateTimeFormatter.format(now) + ".jpg");

        bot.downloadFile(file, tempFile);
        if (tempFile.exists()) {
            addTempPhotoFile(tempFile);
        }
    }

    public File getTempPhotoFile(String date) {
        return tempFiles.get(date);
    }

    public List<File> getTempPhotoFiles() {
        return new ArrayList<>(tempFiles.values());
    }
}
