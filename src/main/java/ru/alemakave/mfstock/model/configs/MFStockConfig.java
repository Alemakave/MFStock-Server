package ru.alemakave.mfstock.model.configs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Scope;
import ru.alemakave.slib.utils.PrintUtils;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Getter
@Scope(scopeName = SCOPE_SINGLETON)
public class MFStockConfig {
    @NotNull
    @JsonProperty
    @Setter
    private String printerName;

    @NotNull
    @JsonProperty
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private DBConfigs dbConfigs;

    @JsonProperty
    private final String[] availablePrinters = PrintUtils.getPrintersName();

    @NotNull
    @JsonProperty
    private UserData[] users;

    @JsonCreator
    public MFStockConfig(@JsonProperty("printerName") @NotNull final String printerName,
                         @JsonProperty("dbConfigs") @NotNull final DBConfigs dbConfigs,
                         @JsonProperty("users") @NotNull final UserData[] users) {
        this.printerName = printerName;
        this.dbConfigs = dbConfigs;
        this.users = users;
    }

    @JsonGetter("dbConfigs")
    public DBConfigs getDBConfigs() {
        return dbConfigs;
    }

    @JsonSetter("dbConfigs")
    public DBConfigs setDBConfigs() {
        return dbConfigs;
    }

    @Override
    public String toString() {
        return String.format("PrinterName: %s, dbConfigs: %s", getPrinterName(), dbConfigs);
    }
}
