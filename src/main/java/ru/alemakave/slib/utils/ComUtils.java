package ru.alemakave.slib.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.extern.slf4j.Slf4j;
import ru.alemakave.slib.PrintConfigurationBuilder;

import java.io.*;

import static ru.alemakave.slib.excel.constants.XlFileFormat.xlHtml;

@Slf4j
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
        Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
        Dispatch workbook = Dispatch.call(workbooks, "Open", file.getAbsolutePath()).toDispatch();
        try {
            Dispatch.put(xl, "Visible", new Variant(false));
            Dispatch worksheets = Dispatch.call(workbook, "Worksheets", 1).toDispatch();
            Dispatch.call(worksheets, "printout", from, to, copies, preview, activePrinter, printToFile, collate, printToFileName, ignorePrintAreas);
            Variant f = new Variant(false);
            Dispatch.call(workbook, "Close", f);
        } catch (Exception e) {
            Variant f = new Variant(false);
            Dispatch.call(workbook, "Close", f);
            xl.invoke("Quit", new Variant[] {});
            ComThread.Release();

            log.error(String.format("%s: %s: %s", e.getClass().getName(), ComUtils.class.getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        } finally {
            xl.invoke("Quit", new Variant[] {});
            ComThread.Release();
        }
    }

    /**
     * <a href="https://learn.microsoft.com/en-us/office/vba/api/excel.workbook.saveas">Documentation</a> [<a href="https://learn.microsoft.com/en-us/office/vba/api/excel.workbook.saveas">RU</a>]
     * @param excelFile input file
     * @param outputFile output file
     */
    public static void saveExcelFileToHtml(File excelFile, File outputFile) {
        ComThread.InitSTA();
        ActiveXComponent xl = new ActiveXComponent("Excel.Application");
        Dispatch workbooks = xl.getProperty("Workbooks").toDispatch();
        Dispatch workbook = Dispatch.call(workbooks, "Open", excelFile.getAbsolutePath()).toDispatch();
        Dispatch.put(xl, "Visible", new Variant(false));
        Dispatch.call(workbook, "SaveAs", outputFile.getAbsolutePath(), xlHtml);
        Variant f = new Variant(false);
        Dispatch.call(workbook, "Close", f);
    }
}
