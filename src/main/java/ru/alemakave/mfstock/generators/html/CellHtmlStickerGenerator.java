package ru.alemakave.mfstock.generators.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.alemakave.mfstock.model.json.sticker.CellSticker;

import java.nio.charset.StandardCharsets;

public class CellHtmlStickerGenerator extends HtmlStickerGeneratorBase<CellSticker> {
    @Override
    public byte[] generate(CellSticker sticker) throws JsonProcessingException {
        String stickerHtml = "";

        stickerHtml += "<div class=\"sticker cell\">";
        stickerHtml += "\t<div class=\"sticker-data cell-address\">" + sticker.getCellAddress() + "</div>";
        stickerHtml += "\t<img class=\"sticker-barcode cell-code\" src=\"/mfstock-generate-barcode?barcodeFormat=CODE_128&width=1&height=1&data=" + sticker.getCellCode() + "\"/>";
        stickerHtml += "\t<div class=\"sticker-data cell-code\">" + sticker.getCellCode() + "</div>";
        stickerHtml += "</div>";

        return stickerHtml.getBytes(StandardCharsets.UTF_8);
    }
}
