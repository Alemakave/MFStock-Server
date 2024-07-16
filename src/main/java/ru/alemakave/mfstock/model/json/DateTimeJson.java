package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Getter
@ToString
public class DateTimeJson implements Comparable<DateTimeJson> {
    @JsonProperty("dateTime")
    private final String dateTimeString;
    @JsonIgnore
    private final SimpleDateFormat dateFormat;

    public DateTimeJson(Date date, SimpleDateFormat dateFormat) {
        this.dateTimeString = dateFormat.format(date);
        this.dateFormat = dateFormat;
    }

    @Override
    public int compareTo(@NotNull DateTimeJson dateTimeJson) {
        return LocalDateTime.parse(getDateTimeString(), DateTimeFormatter.ofPattern(getDateFormat().toPattern()))
                .compareTo(LocalDateTime.parse(dateTimeJson.getDateTimeString(), DateTimeFormatter.ofPattern(getDateFormat().toPattern())));
    }
}
