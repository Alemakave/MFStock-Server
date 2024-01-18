package ru.alemakave.mfstock.model.telegram_bot;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.alemakave.mfstock.telegram_bot.TelegramBotReceiveMode;

import java.util.Objects;

public class TGUserJson {
    private final long tgUserID;
    private int mfUserID;
    private TelegramBotReceiveMode mode = TelegramBotReceiveMode.NONE;

    public TGUserJson(long tgUserID) {
        this.tgUserID = tgUserID;
    }

    @JsonCreator
    public TGUserJson(@JsonProperty("tgUserID") long tgUserID, @JsonProperty("mfUserID") int mfUserID, @JsonProperty("mode") TelegramBotReceiveMode mode) {
        this.tgUserID = tgUserID;
        this.mfUserID = mfUserID;
    }

    public long getTgUserID() {
        return tgUserID;
    }

    public int getMfUserID() {
        return mfUserID;
    }

    public TelegramBotReceiveMode getMode() {
        return mode;
    }

    public void setMfUserID(int mfUserID) {
        this.mfUserID = mfUserID;
    }

    public void setMode(TelegramBotReceiveMode mode) {
        this.mode = mode;
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
