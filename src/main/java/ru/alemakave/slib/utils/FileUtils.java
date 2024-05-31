package ru.alemakave.slib.utils;

public class FileUtils {
    public static String getFileNameWithoutExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
