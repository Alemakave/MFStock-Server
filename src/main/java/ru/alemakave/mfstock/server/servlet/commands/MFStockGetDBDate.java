package ru.alemakave.mfstock.server.servlet.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpMethod;
import ru.alemakave.mfstock.json.JsonSchemeSets;
import ru.alemakave.mfstock.server.MFStockServer;
import ru.alemakave.slib.servlet.IServletCommand;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static ru.alemakave.mfstock.server.MFStockServer.xlsxDatabase;

public class MFStockGetDBDate implements IServletCommand {
    @Override
    public String getCommand() {
        return "mfstock-get-db-date";
    }

    @Override
    public void call(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (xlsxDatabase == null) {
            try {
                MFStockServer.loadDB();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        ServletOutputStream output = resp.getOutputStream();
        resp.setContentType("application/json;charset=UTF-8");
        JsonSchemeSets.DatabaseDateJson databaseDateJson = new JsonSchemeSets.DatabaseDateJson();
        databaseDateJson.date = xlsxDatabase.getDocPropsCore().documentModified.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(output, databaseDateJson);
    }
}
