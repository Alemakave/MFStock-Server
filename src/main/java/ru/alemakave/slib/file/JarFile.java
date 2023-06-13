package ru.alemakave.slib.file;

import java.io.File;
import java.io.IOException;

public class JarFile extends java.util.jar.JarFile {
    public JarFile(String path) throws IOException {
        this(new File(path));
    }

    public JarFile(File file) throws IOException {
        super(file);
    }
}
