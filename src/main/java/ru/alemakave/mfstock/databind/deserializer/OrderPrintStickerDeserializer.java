package ru.alemakave.mfstock.databind.deserializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.OrderNumberSticker;

import java.io.IOException;

public class OrderPrintStickerDeserializer extends PrintStickerDeserializer<OrderNumberSticker> {
    @Override
    public PrintStickerJson<OrderNumberSticker> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        String printer = node.get("SelectPrinter").textValue();
        OrderNumberSticker nomSerSticker = mapper.readValue(node.get("Sticker").toString(), OrderNumberSticker.class);

        return new PrintStickerJson<>(printer, nomSerSticker);
    }
}
