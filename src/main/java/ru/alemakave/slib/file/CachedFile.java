package ru.alemakave.slib.file;

import com.google.common.hash.Hashing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

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

    public String getAbsolutePath() {
        return file.getAbsolutePath();
    }

    public String getName() {
        return file.getName();
    }

    public File getParentFile() {
        return file.getParentFile();
    }

    public void renameTo(File distFile) throws IOException {
        if (!file.renameTo(file)) {
            throw new IOException("File not renamed");
        }
    }

    private void calculateHashCode(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        sha = Hashing
                .sha256()
                .hashBytes(fis.readAllBytes())
                .toString();
        fis.close();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CachedFile)) return false;

        CachedFile that = (CachedFile) o;

        if (!Objects.equals(file, that.file)) return false;
        return Objects.equals(sha, that.sha);
    }

    @Override
    public int hashCode() {
        int result = file != null ? file.hashCode() : 0;
        result = 31 * result + (sha != null ? sha.hashCode() : 0);
        return result;
    }
}
