package ru.alemakave.mfstock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

@Data
public class StickerDto<T extends Sticker> {
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Sticker")
    private T sticker;
}
