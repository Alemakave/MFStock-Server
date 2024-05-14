package ru.alemakave.mfstock.databind.deserializer;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

public abstract class PrintStickerDeserializer<T extends Sticker> extends StdDeserializer<PrintStickerJson<T>> {
    public PrintStickerDeserializer() {
        this(null);
    }

    public PrintStickerDeserializer(Class vc) {
        super(vc);
    }
}
