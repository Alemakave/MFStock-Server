package ru.alemakave.mfstock.databind.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ru.alemakave.mfstock.dto.DownloadExcelStickerFileDto;
import ru.alemakave.mfstock.model.StickerType;
import ru.alemakave.mfstock.model.json.sticker.NomSticker;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

import java.io.IOException;

public class DownloadExcelStickerFileDeserializer extends StdDeserializer<DownloadExcelStickerFileDto> {
    public DownloadExcelStickerFileDeserializer() {
        this(null);
    }

    public DownloadExcelStickerFileDeserializer(Class vc) {
        super(vc);
    }

    @Override
    public DownloadExcelStickerFileDto deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        JsonNode node = p.getCodec().readTree(p);
        StickerType stickerType = mapper.readValue(node.get("StickerType").toString(), StickerType.class);
        Sticker sticker;
        switch (stickerType) {
            case NOM:
                sticker = mapper.readValue(node.get("Sticker").toString(), NomSticker.class);
                break;
            default:
                throw new RuntimeException(String.format("Не удалось запарсить тип \"%s\"", stickerType));
        }

        return new DownloadExcelStickerFileDto(sticker, stickerType);
    }
}
