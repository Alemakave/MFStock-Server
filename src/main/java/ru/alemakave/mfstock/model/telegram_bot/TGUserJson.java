package ru.alemakave.mfstock.model.telegram_bot;

public class TGUserJson {
    private final long tgUserID;
    private int mfUserID;

    public TGUserJson(long tgUserID) {
        this.tgUserID = tgUserID;
    }

    public long getTgUserID() {
        return tgUserID;
    }

    public int getMfUserID() {
        return mfUserID;
    }

    public void setMfUserID(int mfUserID) {
        this.mfUserID = mfUserID;
    }
}
