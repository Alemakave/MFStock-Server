package ru.alemakave.mfstock.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.poi.ss.usermodel.CellType;
import ru.alemakave.mfstock.utils.function.ToHtmlFunction;

import java.util.Objects;

public class TableCell implements ToHtmlFunction {
    public static final TableCell EMPTY = new TableCell("").copy();

    private String value;
    @JsonIgnore
    private CellType cellType;

    public TableCell(@JsonProperty("value") String value, CellType cellType) {
        this.value = value;
        this.cellType = cellType;
    }

    @Deprecated
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

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public TableCell copy() {
        return new TableCell(getValue());
    }

    public boolean isEmpty() {
        return getValue().isEmpty();
    }

    @Override
    public String applyAsHtml() {
        return String.format("<div class=\"table-cell\">%s</div>", value);
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
