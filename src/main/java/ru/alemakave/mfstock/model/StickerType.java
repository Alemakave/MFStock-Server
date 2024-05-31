package ru.alemakave.mfstock.model;

import lombok.Getter;
import ru.alemakave.mfstock.databind.deserializer.*;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

@Getter
public enum StickerType {
    CELL(new CellPrintStickerDeserializer()),
    EMPLOYEE(new EmployeePrintStickerDeserializer()),
    NOM(new NomPrintStickerDeserializer()),
    NOM_SERIAL(new NomSerPrintStickerDeserializer()),
    ORDER_NUMBER(new OrderPrintStickerDeserializer());

    private final PrintStickerDeserializer<? extends Sticker> printStickerDeserializer;

    StickerType(PrintStickerDeserializer<? extends Sticker> printStickerDeserializer) {
        this.printStickerDeserializer = printStickerDeserializer;
    }
}
