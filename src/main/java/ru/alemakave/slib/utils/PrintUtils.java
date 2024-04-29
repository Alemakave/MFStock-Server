package ru.alemakave.slib.utils;

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

            ComUtils.printExcelFile(file, (ExcelPrintConfiguration)printConfigurationBuilder);
        }
    }
}
