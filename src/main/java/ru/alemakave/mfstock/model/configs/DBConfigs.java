package ru.alemakave.mfstock.model.configs;

import java.util.Arrays;

public class DBConfigs {
    private DBConfigsColumns[] columns;

    public DBConfigsColumns[] getColumns() {
        return columns;
    }

    public void setColumns(DBConfigsColumns[] columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return String.format("columns: %s", Arrays.toString(columns));
    }
}
