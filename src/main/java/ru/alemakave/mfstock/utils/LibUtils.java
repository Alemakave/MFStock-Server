package ru.alemakave.mfstock.utils;

import lombok.SneakyThrows;
import ru.alemakave.mfstock.MFStockApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class LibUtils {
    @SneakyThrows
    public static void extractLib(Path libPathInApplicationFile, Path outputPath) {
        if (Files.notExists(outputPath)) {
            String filePath = MFStockApplication.class.getProtectionDomain().getCodeSource().getLocation().getPath().split("!")[0];

            File file = new File(filePath);

            if (filePath.startsWith("/")) {
                file = file.getParentFile().getParentFile().getParentFile();
                file = new File(file, "/resources/main");

                if (libPathInApplicationFile.startsWith("BOOT-INF")) {
                    libPathInApplicationFile = Path.of(libPathInApplicationFile.toString().replace("BOOT-INF" + File.separator + "classes", ""));
                }

                file = new File(file, libPathInApplicationFile.toString());

                System.out.printf("Coping: \"%s\" to \"%s\"", file.getAbsolutePath(), outputPath);
                Files.copy(file.toPath(), outputPath);
            } else {
                file = new File(filePath.substring(5));

                ZipInputStream zis = new ZipInputStream(new FileInputStream(file));

                ZipEntry zipEntry = zis.getNextEntry();
                while (zipEntry != null) {
                    if (zipEntry.getName().equals(libPathInApplicationFile.toString().replace(File.separatorChar, '/'))) {
                        try (FileOutputStream fos = new FileOutputStream(outputPath.toFile())) {
                            System.out.printf("Extracting: \"%s\" to \"%s\"", zipEntry.getName(), outputPath);
                            fos.write(zis.readAllBytes());
                        }
                    }

                    zipEntry = zis.getNextEntry();
                }
            }
        } else {
            System.out.printf("Skipping: \"%s\" (file exists)\n", outputPath);
        }
    }
}
