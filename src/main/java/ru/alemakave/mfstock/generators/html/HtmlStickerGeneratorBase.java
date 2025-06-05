package ru.alemakave.mfstock.generators.html;

import ru.alemakave.mfstock.generators.StickerGenerator;
import ru.alemakave.mfstock.model.StickerFormat;
import ru.alemakave.mfstock.model.json.sticker.Sticker;

public abstract class HtmlStickerGeneratorBase<T extends Sticker> extends StickerGenerator<T> {
    public HtmlStickerGeneratorBase() {
        super(StickerFormat.HTML);
    }
}
