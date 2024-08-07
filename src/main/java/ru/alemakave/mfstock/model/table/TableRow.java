package ru.alemakave.mfstock.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.poi.ss.usermodel.CellType;
import ru.alemakave.mfstock.utils.function.ToHtmlFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableRow implements ToHtmlFunction {
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

    public void addCell(TableCell cell) {
        cells.add(cell);
    }

    @Deprecated
    protected void addCell(String cellValue) {
        cells.add(new TableCell(cellValue, CellType.STRING));
    }

    public void addCell(int index, TableCell cell) {
        while (cells.size() < index) {
            cells.add(TableCell.EMPTY);
        }
        cells.add(index, cell);
    }

    public void setCell(int index, TableCell cell) {
        while (cells.size() <= index) {
            cells.add(TableCell.EMPTY);
        }
        cells.set(index, cell);
    }

    public TableCell getCell(int index) {
        return cells.get(index);
    }

    @JsonIgnore
    public boolean isEmpty() {
        return toString().equals("|".repeat(cells.size()-1));
    }

    @Override
    public String applyAsHtml() {
        String result = "<div class=\"table-row\">\n";

        for (TableCell cell : cells) {
            result += "\t";
            result += cell.applyAsHtml();
            result += "\n";
        }

        result += "</div>";

        return result;
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
