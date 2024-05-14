package ru.alemakave.mfstock.model.json.sticker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class NomSticker implements Sticker {
    @JsonProperty("NomCode")
    private String code;
    @JsonProperty("NomName")
    private String name;
    @JsonProperty("StickerCopies")
    private String copies;
}
