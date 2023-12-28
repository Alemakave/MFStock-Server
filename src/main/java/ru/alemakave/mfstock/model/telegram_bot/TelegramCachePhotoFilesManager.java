package ru.alemakave.mfstock.model.telegram_bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import ru.alemakave.slib.utils.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class TelegramCachePhotoFilesManager {
    private final Map<String, File> tempNomPhotoFiles = new TreeMap<>();
    private final Map<String, File> nomPhotoFiles = new TreeMap<>();

    @Value("${mfstock.photo.cache.path:.}")
    private String photoCacheDir;

    public void addPhotoFile(File file) {
        nomPhotoFiles.put(FileUtils.getFileNameWithoutExtention(file.getName()), file);
    }

    public void addTempPhotoFile(File file) {
        tempNomPhotoFiles.put(FileUtils.getFileNameWithoutExtention(file.getName()), file);
    }

    public File downloadPhotoFile(DefaultAbsSender bot, org.telegram.telegrambots.meta.api.objects.File file) throws Exception {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime now = LocalDateTime.now();

        int photoNumber = 1;
        while (Files.exists(Path.of(photoCacheDir, dateTimeFormatter.format(now) + "_" + photoNumber + "_tmp" + ".jpg"))) {
            photoNumber++;
        }
        File tempFile = new File(photoCacheDir, dateTimeFormatter.format(now) + "_" + photoNumber + "_tmp" + ".jpg");

        if (!tempFile.getParentFile().exists()) {
            Files.createDirectories(tempFile.getParentFile().toPath());
        }

        bot.downloadFile(file, tempFile);
        if (tempFile.exists()) {
            addTempPhotoFile(tempFile);
        }

        return tempFile;
    }

    public File getTempPhotoFile(String date) {
        return tempNomPhotoFiles.get(date);
    }

    public File getPhotoFile(String nomCode) {
        return nomPhotoFiles.get(nomCode);
    }

    public List<File> getTempNomPhotoFiles() {
        return new ArrayList<>(tempNomPhotoFiles.values());
    }

    public List<File> getNomPhotoFiles() {
        return new ArrayList<>(nomPhotoFiles.values());
    }

    @PostConstruct
    private void loadFiles() throws IOException {
        Stream<Path> pathStream = Files.list(Path.of(photoCacheDir));
        pathStream.forEach(path -> {
            String fileName = FileUtils.getFileNameWithoutExtention(path.getFileName().toString());
            if (fileName.endsWith("_tmp")) {
                addTempPhotoFile(new File(path.toUri()));
            } else {
                addPhotoFile(new File(path.toUri()));
            }
        });
        pathStream.close();
    }

    //FIXME: КОД ГОВНО!!!
    public void moveFromTemp(File tmpFile, String nomCode) {
        String destFileName = FileUtils.getFileNameWithoutExtention(tmpFile.getName()).substring(9);
        destFileName = destFileName.substring(0, destFileName.length()-4);

        int photoNumber = 1;
        while (Files.exists(Path.of(photoCacheDir, nomCode + "_" + photoNumber + ".jpg"))) {
            photoNumber++;
        }

        System.out.println(destFileName);
        System.out.println(nomCode + "_" + photoNumber + ".jpg");

        tmpFile.renameTo(new File(tmpFile.getParentFile(), nomCode + "_" + photoNumber + ".jpg"));
    }
}
