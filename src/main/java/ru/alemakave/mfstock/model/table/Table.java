package ru.alemakave.mfstock.model.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.json.DateTimeJson;
import ru.alemakave.mfstock.utils.function.ToHtmlFunction;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.alemakave.slib.utils.ResourceLoader.getLocalFileInputStream;

@Slf4j
public class Table implements ToHtmlFunction {
    private DateTimeJson databaseDate = null;
    private List<TableRow> rows;

    @JsonCreator
    public Table(@JsonProperty("rows") List<TableRow> rows) {
        this.rows = rows;
        databaseDate = new DateTimeJson(Date.from(LocalDateTime.now().toInstant(ZoneOffset.UTC)), new SimpleDateFormat("dd.MM.yyyy HH:mm"));
    }

    public Table(File file) throws IOException {
        init(WorkbookFactory.create(file));
    }

    public Table(InputStream inputStream) throws IOException {
        init(WorkbookFactory.create(inputStream));
    }

    private void init(Workbook workbook) throws IOException {
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
        saveColumnsAccordingHeaders(Arrays.asList(columnsConfig));
    }

    public void saveColumnsAccordingHeaders(List<DBConfigsColumns> columnsConfig) {
        int[] headersIndex = new int[columnsConfig.size()];
        for (int i = 0; i < columnsConfig.size(); i++) {
            final TableCell exceptedTableCell = new TableCell(columnsConfig.get(i).getHeaderText());
            headersIndex[i] = ListUtils.indexOf(rows.get(0).getCells(), object ->
                    object.toString().equalsIgnoreCase(exceptedTableCell.toString())
            );
        }

        saveRowByIndexes(headersIndex);
        System.gc();
    }

    public void setColumnOrder(List<DBConfigsColumns> columnHeader) {
        List<TableRow> orderedRows = new ArrayList<>(getRowsCount());

        int[] headersIndex = new int[columnHeader.size()];
        for (int i = 0; i < columnHeader.size(); i++) {
            final TableCell exceptedTableCell = new TableCell(columnHeader.get(i).getHeaderText());
            headersIndex[i] = ListUtils.indexOf(rows.get(0).getCells(), object ->
                    object.toString().equalsIgnoreCase(exceptedTableCell.toString())
            );
        }

        for (TableRow row : getRows()) {
            TableRow orderedRow = new TableRow();

            for (int i = 0; i < row.getCells().size(); i++) {
                orderedRow.addCell(row.getCells().get(headersIndex[i]));
            }

            orderedRows.add(orderedRow);
        }

        setRows(orderedRows);
    }

    public void addColumnPrefix(DBConfigsColumns... configsColumns) {
        for (DBConfigsColumns configsColumn : configsColumns) {
            if (configsColumn.getPrefix() == null)
                continue;
            List<TableCell> cells = rows.get(0).getCells();
            int columnIndex = cells.indexOf(new TableCell(configsColumn.getHeaderText(), CellType.STRING));
            if (columnIndex > 0) {
                for (int i = 1; i < rows.size(); i++) {
                    TableCell cell = rows.get(i).getCells().get(columnIndex);
                    cell.setValue(configsColumn.getPrefix() + cell.getValue());
                    rows.get(i).getCells().set(columnIndex, cell);
                }
            } else {
                log.warn(String.format("Not found column \"%s\"", configsColumn.getHeaderText()));
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

    public TableRow getRow(int index) {
        return rows.get(index);
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
    }

    public TableRow getHeader() {
        return rows.get(0);
    }

    public void addRows(TableRow... rows) {
        addRows(List.of(rows));
    }

    public void addRows(List<TableRow> rows) {
        this.rows.addAll(rows);
    }

    public void encourage(Table tableToAdd) {
        List<Integer> toColumn = new ArrayList<>();
        List<Integer> fromColumn = new ArrayList<>();
        for (int i = 0; i < getHeader().getCells().size(); i++) {
            TableCell currentTableHeaderCell = getHeader().getCells().get(i);
            for (int j = 0; j < tableToAdd.getHeader().getCells().size(); j++) {
                TableCell tableToAddHeaderCell = tableToAdd.getHeader().getCells().get(j);
                if (currentTableHeaderCell.equals(tableToAddHeaderCell)) {
                    toColumn.add(i);
                    fromColumn.add(j);
                    break;
                }
            }
        }

        for (int i = 1; i < tableToAdd.getRowsCount(); i++) {
            TableRow row = new TableRow();
            for (int j = 0; j < fromColumn.size(); j++) {
                row.setCell(toColumn.get(j), tableToAdd.getRow(i).getCell(fromColumn.get(j)));
            }
            while (row.getCells().size() < getRow(0).getCells().size()) {
                row.addCell(TableCell.EMPTY);
            }
            addRows(row);
        }
    }

    public DateTimeJson getDatabaseDate() {
        return databaseDate;
    }

    @Override
    public String applyAsHtml() {
        try {
            InputStream is = getLocalFileInputStream("/pages/html-parts/upload-table-part.html");
            BufferedInputStream bis = new BufferedInputStream(is);
            Element htmlDocument = Jsoup.parse(
                    new String(
                            bis
                                    .readAllBytes()
                    )
            ).body().child(0);
            bis.close();
            is.close();

            htmlDocument.getElementsByClass("table-header").get(0).children().clear();
            htmlDocument.getElementsByClass("table-content").get(0).children().clear();

            for (int i = 0; i < this.getRowsCount(); i++) {
                if (i == 0) {
                    htmlDocument.getElementsByClass("table-header").get(0).after(rows.get(i).applyAsHtml());

                    htmlDocument.getElementsByClass("table-row").get(0).addClass("table-header");
                    htmlDocument.getElementsByClass("table-row").get(0).removeClass("table-row");

                    htmlDocument.getElementsByClass("table-header").get(0).remove();
                    for (Element child : htmlDocument.getElementsByClass("table-header").get(0).children()) {
                        child.removeClass(child.className());
                        child.addClass("table-header-cell");
                    }
                } else {
                    htmlDocument.getElementsByClass("table-content").get(0).append(rows.get(i).applyAsHtml());
                }
            }

            return htmlDocument.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRowsCount() {
        return rows.size();
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
