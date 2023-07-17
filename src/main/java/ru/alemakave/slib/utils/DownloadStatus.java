package ru.alemakave.slib.utils;

public enum DownloadStatus {
    NOT_FOUND(-1),
    LOADED(0),
    EXISTS(1);

    private int numericStatus;

    DownloadStatus(int numericStatus) {
        this.numericStatus = numericStatus;
    }

    public int getNumericStatus() {
        return numericStatus;
    }
}
