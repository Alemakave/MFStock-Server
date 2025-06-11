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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.alemakave.mfstock.model.UserData;
import ru.alemakave.slib.utils.PrintUtils;

import java.util.Arrays;
import java.util.stream.Stream;

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
    private UserData[] usersData;

    @JsonCreator
    public MFStockConfig(@JsonProperty("printerName") @NotNull final String printerName,
                         @JsonProperty("dbConfigs") @NotNull final DBConfigs dbConfigs,
                         @JsonProperty("users") @NotNull final UserData[] usersData) {
        this.printerName = printerName;
        this.dbConfigs = dbConfigs;
        this.usersData = usersData;
    }

    @JsonGetter("dbConfigs")
    public DBConfigs getDBConfigs() {
        return dbConfigs;
    }

    @JsonSetter("dbConfigs")
    public DBConfigs setDBConfigs() {
        return dbConfigs;
    }

    public UserDetails[] getUsers(PasswordEncoder passwordEncoder) {
        return Arrays.stream(usersData).flatMap(userData ->
                Stream.of(User.builder()
                        .username(userData.getUsername())
                        .password(passwordEncoder.encode(userData.getPassword()))
                        .roles(userData.getRole().toString())
                        .build()
                )
        ).toArray(UserDetails[]::new);
    }

    @Override
    public String toString() {
        return String.format("PrinterName: %s, dbConfigs: %s", getPrinterName(), dbConfigs);
    }
}
