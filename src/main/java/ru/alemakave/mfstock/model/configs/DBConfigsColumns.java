package ru.alemakave.mfstock.model.configs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class DBConfigsColumns {
    private final String headerText;
    private final String prefix;

    @JsonCreator(mode = JsonCreator.Mode.DISABLED)
    public DBConfigsColumns() {
        this(null, null);
    }

    @JsonCreator
    public DBConfigsColumns(@JsonProperty("headerText") String headerText, @JsonProperty("prefix") String prefix) {
        this.headerText = headerText;
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return String.format("headerText: %s, prefix: %s", headerText, prefix);
    }
}