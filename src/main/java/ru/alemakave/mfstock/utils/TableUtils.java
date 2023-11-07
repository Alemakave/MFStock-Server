package ru.alemakave.mfstock.utils;

import ru.alemakave.mfstock.model.table.TableRow;

import java.util.List;
import java.util.stream.Collectors;

public class TableUtils {
    public static List<TableRow> findRowContains(List<TableRow> tableRows, String rowSubstring) {
        return tableRows.stream()
                .filter(tableRow -> tableRow.toString().contains(rowSubstring))
                .collect(Collectors.toList());
    }
}
