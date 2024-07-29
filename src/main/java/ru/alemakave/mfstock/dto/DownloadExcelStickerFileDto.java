package ru.alemakave.mfstock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.alemakave.mfstock.model.StickerType;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

@AllArgsConstructor
@Data
public class DownloadExcelStickerFileDto {
    @JsonProperty("Sticker")
    private Sticker sticker;
    @JsonProperty("StickerType")
    private StickerType stickerType;
}
