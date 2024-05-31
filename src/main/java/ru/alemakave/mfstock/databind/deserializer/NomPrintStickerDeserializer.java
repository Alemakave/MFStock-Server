package ru.alemakave.mfstock.databind.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.NomSticker;

import java.io.IOException;

public class NomPrintStickerDeserializer extends PrintStickerDeserializer<NomSticker> {
    @Override
    public PrintStickerJson<NomSticker> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        String printer = node.get("SelectPrinter").textValue();
        NomSticker nomSerSticker = mapper.readValue(node.get("Sticker").toString(), NomSticker.class);

        return new PrintStickerJson<>(printer, nomSerSticker);
    }
}
