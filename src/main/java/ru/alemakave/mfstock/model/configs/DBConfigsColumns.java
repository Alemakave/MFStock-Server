package ru.alemakave.mfstock.model.configs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DBConfigsColumns {
    private final String headerText;
    private final String prefix;

    @JsonCreator
    public DBConfigsColumns(@JsonProperty("headerText") String headerText, @JsonProperty("prefix") String prefix) {
        this.headerText = headerText;
        this.prefix = prefix;
    }

    public String getHeaderText() {
        return headerText;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public String toString() {
        return String.format("headerText: %s, prefix: %s", headerText, prefix);
    }
}