package ru.alemakave.slib;

public class PrintConfigurationBuilder {
    public static ExcelPrintConfiguration buildExcelConfiguration(String printerName) {
        return new ExcelPrintConfiguration(printerName);
    }

    public static class PrintConfiguration {
        private final String printerName;
        private int copies = 1;

        private PrintConfiguration(String printerName) {
            this.printerName = printerName;
        }

        public String getPrinterName() {
            return printerName;
        }

        public int getCopies() {
            return copies;
        }

        public PrintConfiguration setCopies(int copies) {
            this.copies = copies;
            return this;
        }
    }

    public static class ExcelPrintConfiguration extends PrintConfiguration {
        private int from = 1;
        private int to = 1;
        private boolean preview = false;
        private boolean printToFile = false;
        private boolean collate = false;
        private String printToFileName = "";
        private boolean ignorePrintAreas = false;

        private ExcelPrintConfiguration(String printerName) {
            super(printerName);
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }

        public boolean isPreview() {
            return preview;
        }

        public boolean isPrintToFile() {
            return printToFile;
        }

        public boolean isCollate() {
            return collate;
        }

        public String getPrintToFileName() {
            return printToFileName;
        }

        public boolean isIgnorePrintAreas() {
            return ignorePrintAreas;
        }

        public ExcelPrintConfiguration setFrom(int from) {
            this.from = from;
            return this;
        }

        public ExcelPrintConfiguration setTo(int to) {
            this.to = to;
            return this;
        }

        public ExcelPrintConfiguration setPreview(boolean preview) {
            this.preview = preview;
            return this;
        }

        public ExcelPrintConfiguration setPrintToFile(boolean printToFile) {
            this.printToFile = printToFile;
            return this;
        }

        public ExcelPrintConfiguration setCollate(boolean collate) {
            this.collate = collate;
            return this;
        }

        public ExcelPrintConfiguration setPrintToFileName(String printToFileName) {
            this.printToFileName = printToFileName;
            return this;
        }

        public ExcelPrintConfiguration setIgnorePrintAreas(boolean ignorePrintAreas) {
            this.ignorePrintAreas = ignorePrintAreas;
            return this;
        }
    }
}
