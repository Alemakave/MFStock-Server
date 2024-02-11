package ru.alemakave.mfstock.telegram_bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alemakave.mfstock.telegram_bot.exception.PhotoAlreadyAdded;
import ru.alemakave.slib.file.CachedFile;
import ru.alemakave.slib.utils.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class TelegramCachePhotoFilesManager {
    @Deprecated
    private final Map<String, File> tempNomPhotoFiles = new TreeMap<>();
    @Deprecated
    private final Map<String, File> nomPhotoFiles = new TreeMap<>();

    private final Map<String, CachedFile> nomPhotos = new TreeMap<>();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${mfstock.photo.cache.path:.}")
    private String photoCacheDir;

    public void addPhoto(CachedFile file) throws PhotoAlreadyAdded {
        for (CachedFile cachedFile : nomPhotos.values()) {
            if (cachedFile.getSha().equals(file.getSha())) {
                throw new PhotoAlreadyAdded("Photo already added!");
            }
        }

        nomPhotos.put(FileUtils.getFileNameWithoutExtention(file.getName()), file);
    }

    public CachedFile getPhoto(String photoFileName) {
        return nomPhotos.get(FileUtils.getFileNameWithoutExtention(photoFileName));
    }

    @Deprecated
    public void addPhotoFile(File file) {
        nomPhotoFiles.put(FileUtils.getFileNameWithoutExtention(file.getName()), file);
    }

    @Deprecated
    public void addTempPhotoFile(File file) {
        tempNomPhotoFiles.put(FileUtils.getFileNameWithoutExtention(file.getName()), file);
    }

    /**
     * @deprecated Moved to telegram bot class
     */
    @Deprecated
    public File downloadPhotoFile(TelegramBot bot, org.telegram.telegrambots.meta.api.objects.File file) throws Exception {
        /*DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
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
         */
        File tempFile = bot.downloadPhoto(file).getFile();
        addTempPhotoFile(tempFile);

        return tempFile;
    }

    @Deprecated
    public File getTempPhotoFile(String date) {
        return tempNomPhotoFiles.get(date);
    }

    @Deprecated
    public File getPhotoFile(String nomCode) {
        return nomPhotoFiles.get(nomCode);
    }

    @Deprecated
    public List<File> getTempNomPhotoFiles() {
        return new ArrayList<>(tempNomPhotoFiles.values());
    }

    @Deprecated
    public List<File> getNomPhotoFiles() {
        return new ArrayList<>(nomPhotoFiles.values());
    }

    //TODO: Переработать
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

    //FIXME: Переработать
    public void moveFromTemp(File tmpFile, String nomCode) {
        String destFileName = FileUtils.getFileNameWithoutExtention(tmpFile.getName()).substring(9);
        destFileName = destFileName.substring(0, destFileName.length()-4);

        int photoNumber = 1;
        while (Files.exists(Path.of(photoCacheDir, nomCode + "_" + photoNumber + ".jpg"))) {
            photoNumber++;
        }

        logger.debug(destFileName);
        logger.debug(nomCode + "_" + photoNumber + ".jpg");

        tmpFile.renameTo(new File(tmpFile.getParentFile(), nomCode + "_" + photoNumber + ".jpg"));
    }
}
