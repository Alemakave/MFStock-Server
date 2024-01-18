package ru.alemakave.slib;

import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class CachedFile {
    private final File file;
    private String sha;

    public CachedFile(File file) {
        this.file = file;
        try {
            calculateHashCode(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public File getFile() {
        return file;
    }

    public String getSha() {
        return sha;
    }

    private void calculateHashCode(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        sha = Hashing
                .sha256()
                .hashBytes(fis.readAllBytes())
                .toString();
        fis.close();
    }
}