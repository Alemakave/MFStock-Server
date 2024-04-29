package ru.alemakave.slib.utils;

import java.io.InputStream;

public class ResourceLoader {
    public static InputStream getLocalFileInputStream(String classPathLocation) {
        return ResourceLoader.class.getResourceAsStream(classPathLocation);
    }
}
