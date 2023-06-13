package ru.alemakave.xlsx_parser;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import org.w3c.dom.Element;
import ru.alemakave.slib.utils.Logger;

@JsonAutoDetect(
        fieldVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE
)
public class SheetCell {
    public String value;

    public SheetCell(String cellValue) {
        value = cellValue;
    }

    public SheetCell(Workbook workbook, int valueIndex) {
        if (valueIndex < 0)
            this.value = "";
        else
            this.value = workbook.cellValues.get(valueIndex);
    }

    public static SheetCell parse(Workbook workbook, Element xmlElement) {
        Logger.debug(xmlElement.getTextContent());
        if (!xmlElement.getTextContent().isEmpty())
            return new SheetCell(workbook, Integer.parseInt(xmlElement.getElementsByTagName("v").item(0).getTextContent()));
        else
            return new SheetCell(workbook, -1);
    }

    @Override
    public String toString() {
        return value;
    }
}
