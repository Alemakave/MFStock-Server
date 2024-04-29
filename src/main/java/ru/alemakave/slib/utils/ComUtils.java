package ru.alemakave.slib.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import ru.alemakave.slib.PrintConfigurationBuilder;

import java.io.*;

public class ComUtils {
    public static void printExcelFile(File file, PrintConfigurationBuilder.ExcelPrintConfiguration printConfigurationBuilder) {
        int from = printConfigurationBuilder.getFrom();
        int to = printConfigurationBuilder.getTo();
        int copies = printConfigurationBuilder.getCopies();
        boolean preview = printConfigurationBuilder.isPreview();
        String activePrinter = printConfigurationBuilder.getPrinterName();
        boolean printToFile = printConfigurationBuilder.isPrintToFile();
        boolean collate = printConfigurationBuilder.isCollate();
        String printToFileName = printConfigurationBuilder.getPrintToFileName();
        boolean ignorePrintAreas = printConfigurationBuilder.isIgnorePrintAreas();

        ComThread.InitSTA();

        ActiveXComponent xl = new ActiveXComponent("Excel.Application");
        try {
            Dispatch.put(xl, "Visible", new Variant(false));
            Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
            Dispatch workbook = Dispatch.call(workbooks, "Open", file.getAbsolutePath()).toDispatch();
            Dispatch worksheets = Dispatch.call(workbook, "Worksheets", 1).toDispatch();
            Dispatch.call(worksheets, "printout", from, to, copies, preview, activePrinter, printToFile, collate, printToFileName, ignorePrintAreas);
            Variant f = new Variant(false);
            Dispatch.call(workbook, "Close", f);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            xl.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
    }
}
