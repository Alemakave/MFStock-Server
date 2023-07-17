package ru.alemakave.slib.utils;

import java.io.*;
import java.net.*;

public class Downloader {
    private boolean checkDownloadedFile = true;
    private boolean isAutoCreateTreeDirs = false;
    private boolean useConvertUrlToASCII = true;
    private int size = -1;
    private URL downloadFileUrl;

    public Downloader(String downloadFileUrl) {
        try {
            this.downloadFileUrl = new URL(downloadFileUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Downloader(URL downloadFileUrl) {
        this.downloadFileUrl = downloadFileUrl;
    }

    private Downloader(boolean checkDownloadedFile) {
        this.checkDownloadedFile = checkDownloadedFile;
    }

    public void setCheckFile(boolean value) {
        checkDownloadedFile = value;
    }

    public void setUseConvertUrlToASCII(boolean value) {
        useConvertUrlToASCII = value;
    }

    protected boolean check(String filePath) {
        return check(new File(filePath));
    }

    protected boolean check(File file) {
        boolean ret;
        if (getFileSizeInServer() > file.length())
            ret = false;
        else
            ret = file.exists();
        return ret;
    }

    public int getFileSizeInServer() {
        if (size == -1) {
            try {
                size = new URL(new URI(downloadFileUrl.getProtocol(), downloadFileUrl.getHost(), downloadFileUrl.getFile(), null).toASCIIString()).openConnection().getContentLength();
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return size;
    }

    /**
     * @param filePath path to file
     * @return Downloaded status
     */
    public DownloadStatus download(String filePath) {
        return download(new File(filePath));
    }

    public DownloadStatus download(File file) {
        try {
            if (checkDownloadedFile && check(file))
                return DownloadStatus.EXISTS;
            else if (!checkDownloadedFile && file.exists())
                file.delete();
            if (isAutoCreateTreeDirs) {
                File path = file.getParentFile();
                if (!path.exists())
                    path.mkdirs();
            }
            URI uri = new URI(downloadFileUrl.getProtocol(), downloadFileUrl.getHost(), downloadFileUrl.getFile(), null);
            if (!file.exists()) file.createNewFile();
            String uriString;
            if (useConvertUrlToASCII)
                uriString = uri.toASCIIString();
            else
                uriString = downloadFileUrl.toString();
            HttpURLConnection connection = (HttpURLConnection) new URL(uriString).openConnection();
            int size = connection.getContentLength();
            this.size = size;
            if (size == -1) {
                this.size = getFileSizeInServer();
                size = this.size;
            }
            BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
            FileOutputStream fis = new FileOutputStream(file);
            byte[] buffer = new byte[100 * 1024 * 1024];
            long dl = 0;
            int count;
            while ((count = bis.read(buffer, 0, buffer.length)) != -1) {
                updateProgress(file.getName(), dl, size);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fis.write(buffer, 0, count);
                dl += count;
            }
            updateProgress(file.getName(), dl, size);
            fis.close();
            bis.close();
        } catch (FileNotFoundException e1) {
            Logger.errorF("File \"%s\" not fount in server! (URL: %s)\n", file.getName(), downloadFileUrl.toString());
            file.delete();
            return DownloadStatus.NOT_FOUND;
        } catch (URISyntaxException | IOException e) {
            Logger.fatal(e);
        }
        return DownloadStatus.LOADED;
    }

    public void updateProgress(String fileName, long a, long b) {
        Logger.infoF("Download file \"%s\": %d/%d", fileName, a, b);
    }

    public void setAutoCreateTreeDirs(boolean autoCreateTreeDirs) {
        isAutoCreateTreeDirs = autoCreateTreeDirs;
    }
}