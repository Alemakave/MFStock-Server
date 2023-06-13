package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.InputStream;

import static ru.alemakave.slib.utils.XMLUtils.readXMLDocumentFromInputStream;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class Sheet {
    private final Workbook workbook;
    public SheetData sheetData;

    public Sheet(Workbook workbook, InputStream dataStream) {
        this.workbook = workbook;
        try {
            load(dataStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getValue(int row, int column) {
        return sheetData.getValue(row, column);
    }

    private void load(InputStream stream) throws Exception {
        Document sheetDocument = readXMLDocumentFromInputStream(stream);
        sheetData = SheetData.parse(workbook, (Element) sheetDocument.getElementsByTagName("sheetData").item(0));
    }

    @Override
    public String toString() {
        return sheetData.toString();
    }
}
