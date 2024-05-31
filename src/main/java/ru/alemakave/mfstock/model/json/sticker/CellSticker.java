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
public class CellSticker implements Sticker {
    @JsonProperty("CellAddress")
    private String cellAddress;
    @JsonProperty("CellCode")
    private String cellCode;
}
