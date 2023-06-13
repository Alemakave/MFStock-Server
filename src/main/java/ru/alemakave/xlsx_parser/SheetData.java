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
public class SheetData {
    public ArrayList<SheetRow> rows;

    public SheetData(ArrayList<SheetRow> rows) {
        this.rows = rows;
    }

    public String getValue(int row, int column) {
        return rows.get(row).getCell(column).value;
    }

    public void addColumnDataPrefix(String columnHeader, String cellPrefix) {
        for (int i = 1; i < rows.size(); i++) {
            SheetRow row = rows.get(i);
            ArrayList<SheetCell> rowCells = row.cells;
            for (int j = 0; j < rowCells.size(); j++) {
                if (rows.get(0).cells.get(j).value.equals(columnHeader)) {
                    SheetCell cell = row.cells.get(j);
                    cell.value = cellPrefix + cell.value;
                }
            }
        }
    }

    public void filterColumns(ArrayList<String> columnHeaders) {
        ArrayList<SheetRow> newRows = new ArrayList<>();

        for (SheetRow row : rows) {
            ArrayList<SheetCell> newRowCells = new ArrayList<>();

            ArrayList<SheetCell> rowCells = row.cells;
            for (String columnHeader : columnHeaders) {
                for (int j = 0; j < rowCells.size(); j++) {
                    if (rows.get(0).cells.get(j).value.equals(columnHeader)) {
                        newRowCells.add(row.cells.get(j));
                        break;
                    }
                }
            }

            SheetRow newRow = new SheetRow(newRowCells);
            newRows.add(newRow);
        }

        rows.clear();
        rows.addAll(newRows);
        System.gc();
    }

    public static SheetData parse(Workbook workbook, Element xmlElement) {
        ArrayList<SheetRow> rows = new ArrayList<>();

        NodeList rowList = xmlElement.getElementsByTagName("row");
        for (int i = 0; i < rowList.getLength(); i++) {
            SheetRow row = SheetRow.parse(workbook, (Element)rowList.item(i));
            if (!row.isEmptyCells())
                rows.add(row);
        }

        return new SheetData(rows);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (SheetRow row : rows) {
            sb.append(row).append("\n");
        }

        return sb.substring(0, sb.length()-1);
    }
}
