package ru.alemakave.mfstock.generators;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.alemakave.mfstock.model.StickerFormat;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

@Getter
@ToString
@EqualsAndHashCode
public abstract class StickerGenerator<T extends Sticker> {
    private final StickerFormat format;

    public StickerGenerator(StickerFormat format) {
        this.format = format;
    }

    public abstract byte[] generate(T sticker) throws JsonProcessingException;
}
