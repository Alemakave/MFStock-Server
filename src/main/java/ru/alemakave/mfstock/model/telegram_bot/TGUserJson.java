package ru.alemakave.mfstock.model.telegram_bot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TGUserJson {
    private final long tgUserID;
    private int mfUserID;

    public TGUserJson(long tgUserID) {
        this.tgUserID = tgUserID;
    }

    @JsonCreator
    public TGUserJson(@JsonProperty("tgUserID") long tgUserID, @JsonProperty("mfUserID") int mfUserID) {
        this.tgUserID = tgUserID;
        this.mfUserID = mfUserID;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TGUserJson that = (TGUserJson) o;
        return tgUserID == that.tgUserID && mfUserID == that.mfUserID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tgUserID, mfUserID);
    }
}
