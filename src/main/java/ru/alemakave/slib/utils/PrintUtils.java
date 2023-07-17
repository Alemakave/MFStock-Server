package ru.alemakave.slib.utils;

import ru.alemakave.slib.exception.PrinterNotFoundException;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

public class PrintUtils {
    public static PrintService[] getPrinters() {
        return PrintServiceLookup.lookupPrintServices(DocFlavor.INPUT_STREAM.AUTOSENSE, null);
    }

    public static PrintService getPrinterByName(String printerName) throws PrinterNotFoundException {
        for (PrintService printer :
                getPrinters()) {
            if (printer.getName().equalsIgnoreCase(printerName)) {
                return printer;
            }
        }

        throw new PrinterNotFoundException(String.format("Printer \"%s\" not founded!", printerName));
    }
}
