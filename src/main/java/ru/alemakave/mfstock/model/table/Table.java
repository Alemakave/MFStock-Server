package ru.alemakave.mfstock.model.table;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.alemakave.mfstock.configs.model.DBConfigsColumns;
import ru.alemakave.mfstock.model.json.DateTimeJson;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Table {
    private DateTimeJson databaseDate = null;
    private List<TableRow> rows;

    public Table(List<TableRow> rows) {
        this.rows = rows;
    }

    public Table(File file) throws IOException {
        init(file);
    }

    private void init(File file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file);
        databaseDate = new DateTimeJson(((XSSFWorkbook)workbook).getProperties().getCoreProperties().getModified(), new SimpleDateFormat("dd.MM.yyyy HH:mm"));
        Sheet sheet = workbook.getSheetAt(0);
        rows = StreamSupport.stream(sheet.spliterator(), false)
                .collect(RowCollector.instance());
        workbook.close();
    }

    public void saveRowAccordingFilter(Predicate<TableRow> filter) {
        rows = rows.stream()
                .filter(filter)
                .collect(Collectors.toList());
        System.gc();
    }

    public void saveColumnsAccordingHeaders(DBConfigsColumns... columnsConfig) {
        int[] headersIndex = new int[columnsConfig.length];
        for (int i = 0; i < columnsConfig.length; i++) {
            headersIndex[i] = rows.get(0).getCells().indexOf(new TableCell(columnsConfig[i].getHeaderText()));
        }

        saveRowByIndexes(headersIndex);
        System.gc();
    }

    //TODO: Добавить получение индексов колонок по заголовку
    public void postInit() {
        int nomCodeIndex = 0;
        int nomSerIndex = 3;
        int nomCountIndex = 4;
        int nomCellAddressIndex = 6;

        for (int i = 0; i < rows.size(); i++) {
            for (int j = i+1; j < rows.size(); j++) {
                List<TableCell> cells1 = rows.get(i).getCells();
                List<TableCell> cells2 = rows.get(j).getCells();
                if (cells1 == null || cells2 == null) {
                    continue;
                }

                if (cells1.get(nomCodeIndex).equals(cells2.get(nomCodeIndex))
                        && cells1.get(nomSerIndex).equals(cells2.get(nomSerIndex))
                        && cells1.get(nomCellAddressIndex).equals(cells2.get(nomCellAddressIndex))) {
                    TableCell countCell = cells1.get(nomCountIndex);
                    countCell.setValue(Integer.toString(Integer.parseInt(countCell.getValue()) + Integer.parseInt(cells2.get(nomCountIndex).getValue())));
                    rows.get(j).setCells(null);
                }
            }
        }

        rows = rows.stream()
                .filter(tableRow -> tableRow.getCells() != null)
                .collect(Collectors.toList());
    }

    public void addColumnPrefix(DBConfigsColumns... configsColumns) {
        for (DBConfigsColumns configsColumn : configsColumns) {
            if (configsColumn.getPrefix() == null)
                continue;
            List<TableCell> cells = rows.get(0).getCells();
            int columnIndex = cells.indexOf(new TableCell(configsColumn.getHeaderText()));
            for (int i = 1; i < rows.size(); i++) {
                TableCell cell = rows.get(i).getCells().get(columnIndex);
                cell.setValue(configsColumn.getPrefix() + cell.getValue());
                rows.get(i).getCells().set(columnIndex, cell);
            }
        }
    }

    private void saveRowByIndexes(int[] headersIndex) {
        for (TableRow row : rows) {
            ArrayList<TableCell> newRowCells = new ArrayList<>(row.getCells().size());
            for (int i = 0; i < row.getCells().size(); i++) {
                for (int index : headersIndex) {
                    if (i == index) {
                        newRowCells.add(row.getCells().get(i));
                    }
                }
            }
            row.setCells(newRowCells);
        }
    }

    public List<TableRow> getRows() {
        return rows;
    }

    public DateTimeJson getDatabaseDate() {
        return databaseDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table)) return false;

        Table table = (Table) o;

        return Objects.equals(rows, table.rows);
    }

    @Override
    public int hashCode() {
        return rows != null ? rows.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < rows.size(); i++) {
            stringBuilder.append(rows.get(i));

            if (i < rows.size()-1) {
                stringBuilder.append("\n");
            }
        }

        return stringBuilder.toString();
    }
}
