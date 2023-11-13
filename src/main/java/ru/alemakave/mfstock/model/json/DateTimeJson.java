package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.util.Date;

public class DateTimeJson {
    @JsonProperty("dateTime")
    private String dateTimeString;

    public DateTimeJson(FileTime time, DateFormat dateFormat) {
        this(Date.from(time.toInstant()), dateFormat);
    }

    public DateTimeJson(Date date, DateFormat dateFormat) {
        this.dateTimeString = dateFormat.format(date);
    }

    public String getDateTimeString() {
        return dateTimeString;
    }

    public void setDateTimeString(FileTime time, DateFormat dateFormat) {
        this.dateTimeString = dateFormat.format(time);
    }

    @Override
    public String toString() {
        return "{" +
                "dateTimeString='" + dateTimeString + '\'' +
                '}';
    }
}
