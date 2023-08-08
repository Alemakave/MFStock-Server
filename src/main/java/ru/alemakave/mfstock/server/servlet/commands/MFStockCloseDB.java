package ru.alemakave.mfstock.server.servlet.commands;

import ru.alemakave.slib.servlet.IServletCommand;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public abstract class MFStockCloseDB implements IServletCommand {
    private final String pagePath = "pages/MFStockCloseDBStatus.html";
    private final InputStream MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM;
    private final String pageData;

    public MFStockCloseDB() throws IOException {
        this.MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM = this.getClass().getProtectionDomain().getClassLoader().getResourceAsStream(pagePath);

        if (this.MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM == null) {
            throw new FileNotFoundException(String.format("MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM \"%s\" not found!", pagePath));
        }
        BufferedInputStream fis = new BufferedInputStream(this.MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM);
        pageData = new String(fis.readAllBytes());
        fis.close();
        this.MFSTOCK_CLOSE_DB_STATUS_INPUT_STREAM.close();
    }

    @Override
    public String getCommand() {
        return "mfstock-close-db";
    }

    public String getPageData() {
        return pageData;
    }
}
