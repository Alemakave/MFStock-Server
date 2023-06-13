package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SheetRow {
    public ArrayList<SheetCell> cells;

    public SheetRow(ArrayList<SheetCell> cells) {
        this.cells = cells;
    }

    public SheetCell getCell(int column) {
        return cells.get(column);
    }

    public static SheetRow parse(Workbook workbook, Element xmlElement) {
        ArrayList<SheetCell> cells = new ArrayList<>();

        NodeList cellList = xmlElement.getElementsByTagName("c");
        for (int i = 0; i < cellList.getLength(); i++) {
            cells.add(SheetCell.parse(workbook,(Element)cellList.item(i)));
        }

        return new SheetRow(cells);
    }

    public boolean isEmptyCells() {
        return toString().equals("|".repeat(cells.size()-1));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (SheetCell cell : cells) {
            sb.append(cell).append("|");
        }

        return sb.substring(0, sb.length()-1);
    }
}