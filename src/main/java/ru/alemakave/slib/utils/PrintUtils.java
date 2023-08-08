package ru.alemakave.slib.utils;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.File;
import java.io.FileNotFoundException;

public class PrintUtils {
    public static PrintService[] getPrinters() {
        return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
    }

    public static void printFile(File file, String printerName) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(String.format("File not found: \"%s\"", file.getAbsolutePath()));
        }

        if (file.getName().endsWith(".xls") || file.getName().endsWith(".xlsx") || file.getName().endsWith(".xlt")) {
            printExcelFile(file, printerName);
        }
    }

    private static void printExcelFile(File file, String activePrinter) {
        int from = 1;
        int to = 1;
        int copies = 1;
        boolean preview = false;
        boolean printToFile = false;
        boolean collate = false;
        String printToFileName = "";
        boolean ignorePrintAreas = false;

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
