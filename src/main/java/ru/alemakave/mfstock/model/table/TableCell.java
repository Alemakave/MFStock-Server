package ru.alemakave.mfstock.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class TableCell {
    private String value;

    @JsonCreator
    public TableCell(@JsonProperty("value") String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof String) return getValue().equals(o);
        if (!(o instanceof TableCell)) return false;

        TableCell tableCell = (TableCell) o;

        return Objects.equals(value, tableCell.value);
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }

    @Override
    public String toString() {
        return getValue();
    }
}
