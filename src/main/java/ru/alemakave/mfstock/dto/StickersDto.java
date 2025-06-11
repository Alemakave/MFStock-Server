package ru.alemakave.mfstock.dto;

import lombok.Data;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

import java.util.List;

@Data
public class StickersDto<T extends Sticker> {
    private List<StickerDto<T>> stickers;
}