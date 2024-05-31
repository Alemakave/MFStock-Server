package ru.alemakave.mfstock.model.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PrintStickerJson<T extends Sticker> {
    @JsonProperty("SelectPrinter")
    private String selectPrinter;
    @JsonProperty("Sticker")
    private T sticker;
}
