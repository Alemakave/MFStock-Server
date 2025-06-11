package ru.alemakave.mfstock.model.configs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

@Getter
@Setter
public class DBConfigs {
    @NotNull
    @JsonProperty
    private DBConfigsColumns[] columns;

    @JsonCreator
    public DBConfigs(@JsonProperty("columns") @NotNull final DBConfigsColumns[] columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return String.format("columns: %s", Arrays.toString(columns));
    }
}
