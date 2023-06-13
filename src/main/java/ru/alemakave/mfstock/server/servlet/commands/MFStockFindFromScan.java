package ru.alemakave.mfstock.server.servlet.commands;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.slib.servlet.IServletCommand;
import ru.alemakave.slib.utils.Logger;
import ru.alemakave.slib.utils.StringUtils;
import ru.alemakave.xlsx_parser.SheetData;
import ru.alemakave.xlsx_parser.SheetRow;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

import static ru.alemakave.mfstock.server.MFStockServer.xlsxDatabase;
import static ru.alemakave.mfstock.server.utils.PageUtils.getCommandValueFromKey;

public class MFStockFindFromScan implements IServletCommand {
    @Override
    public String getCommand() {
        return "mfstock-find-from-scan";
    }

    @Override
    public void call(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletOutputStream output = resp.getOutputStream();
        resp.setContentType("application/json;charset=UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<SheetRow> rows = xlsxDatabase.getWorkbook().sheets.get(0).sheetData.rows;
        SheetData result = new SheetData(new ArrayList<>());

        String commandValue = StringUtils.customUnquote(getCommandValueFromKey(req, "mfstock-find-from-scan"));
        String[] commandValueParts = commandValue.split("%23");
        for (int i = 0; i < rows.size(); i++){
            SheetRow row  = rows.get(i);
            if (i < 1) {
                result.rows.add(row);
            }
            else if (row.toString().contains(commandValueParts[0])) {
                if (commandValueParts.length == 1 || row.toString().contains(commandValueParts[1]))
                    result.rows.add(row);
            }
        }
        mapper.writeValue(output, result);
        Logger.info(result.toString());
    }
}
