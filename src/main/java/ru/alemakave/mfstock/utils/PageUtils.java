package ru.alemakave.mfstock.utils;

import org.springframework.core.io.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;

public final class PageUtils {
    public static String getPage(Resource pageResource) {
        try (BufferedInputStream inputStream = new BufferedInputStream(pageResource.getInputStream())) {
            return new String(inputStream.readAllBytes());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}
