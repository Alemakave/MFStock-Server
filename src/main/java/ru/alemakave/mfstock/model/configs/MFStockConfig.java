package ru.alemakave.mfstock.model.configs;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.alemakave.slib.utils.PrintUtils;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class MFStockConfig {
    private String printerName;
    private DBConfigs dbConfigs;
    @JsonProperty
    private String[] availablePrinters = PrintUtils.getPrintersName();

    public String getPrinterName() {
        return printerName;
    }

    public DBConfigs getDBConfigs() {
        return dbConfigs;
    }

    public String[] getAvailablePrinters() {
        return availablePrinters;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public void setDBConfigs(DBConfigs dbConfigs) {
        this.dbConfigs = dbConfigs;
    }

    @Override
    public String toString() {
        return String.format("PrinterName: %s, dbConfigs: %s", getPrinterName(), dbConfigs);
    }
}
