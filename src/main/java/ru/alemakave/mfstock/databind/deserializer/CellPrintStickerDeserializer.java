package ru.alemakave.mfstock.databind.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.CellSticker;

import java.io.IOException;

public class CellPrintStickerDeserializer extends PrintStickerDeserializer<CellSticker> {
    @Override
    public PrintStickerJson<CellSticker> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        String printer = node.get("SelectPrinter").textValue();
        CellSticker nomSerSticker = mapper.readValue(node.get("Sticker").toString(), CellSticker.class);

        return new PrintStickerJson<>(printer, nomSerSticker);
    }
}
