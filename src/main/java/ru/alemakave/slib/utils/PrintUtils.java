package ru.alemakave.slib.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import ru.alemakave.slib.PrintConfigurationBuilder.ExcelPrintConfiguration;
import ru.alemakave.slib.PrintConfigurationBuilder.PrintConfiguration;

import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.File;
import java.io.FileNotFoundException;

public class PrintUtils {
    public static PrintService[] getPrinters() {
        return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
    }

    public static String[] getPrintersName() {
        PrintService[] printers = getPrinters();
        String[] result = new String[printers.length];

        for (int i = 0; i < printers.length; i++) {
            result[i] = printers[i].getName();
        }

        return result;
    }

    public static void printFile(File file, PrintConfiguration printConfigurationBuilder) throws FileNotFoundException, PrintException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File not found: \"%s\"", file.getAbsolutePath()));
        }

        if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx") || file.getName().endsWith(".xlt")) {
            if (!(printConfigurationBuilder instanceof ExcelPrintConfiguration)) {
                throw new PrintException("Unsupported print configuration for excel file.");
            }

            printExcelFile(file, (ExcelPrintConfiguration)printConfigurationBuilder);
        }
    }

    private static void printExcelFile(File file, ExcelPrintConfiguration printConfigurationBuilder) {
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
