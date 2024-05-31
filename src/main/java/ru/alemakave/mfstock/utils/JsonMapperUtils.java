package ru.alemakave.mfstock.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import ru.alemakave.mfstock.databind.deserializer.PrintStickerDeserializer;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

public class JsonMapperUtils {
    public static ObjectMapper getMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    public static ObjectMapper getStickerDeserializedMapper(PrintStickerDeserializer<? extends Sticker> stickerDeserializer) {
        ObjectMapper mapper = getMapper();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, stickerDeserializer);
        mapper.registerModule(module);

        return mapper;
    }
}
