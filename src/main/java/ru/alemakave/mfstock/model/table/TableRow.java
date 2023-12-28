package ru.alemakave.mfstock.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableRow {
    private List<TableCell> cells;

    public TableRow() {
        this.cells = new ArrayList<>();
    }

    @JsonCreator
    public TableRow(@JsonProperty("cells") List<TableCell> cells) {
        this.cells = cells;
    }

    public List<TableCell> getCells() {
        return cells;
    }

    public void setCells(List<TableCell> cells) {
        this.cells = cells;
    }

    protected void addCell(String cellValue) {
        cells.add(new TableCell(cellValue));
    }

    @JsonIgnore
    public boolean isEmpty() {
        return toString().equals("|".repeat(cells.size()-1));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TableRow)) return false;

        TableRow tableRow = (TableRow) o;

        return Objects.equals(cells, tableRow.cells);
    }

    @Override
    public int hashCode() {
        return cells != null ? cells.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        for (TableCell cell : getCells()) {
            result.append(cell.getValue());
            result.append("|");
        }

        if (getCells().size() > 0)
            return result.substring(0, result.length()-1);
        else
            return result.toString();
    }
}
