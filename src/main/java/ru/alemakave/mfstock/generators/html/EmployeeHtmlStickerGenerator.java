package ru.alemakave.mfstock.generators.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.alemakave.mfstock.model.json.sticker.EmployeeSticker;

import java.nio.charset.StandardCharsets;

public class EmployeeHtmlStickerGenerator extends HtmlStickerGeneratorBase<EmployeeSticker> {
    @Override
    public byte[] generate(EmployeeSticker sticker) throws JsonProcessingException {
        String stickerHtml = "";

        stickerHtml += "<div class=\"sticker employee\">";
        stickerHtml += "\t<div class=\"sticker-data employee-name\">" + sticker.getName() + "</div>";
        stickerHtml += "\t<img class=\"sticker-barcode employee-code\" src=\"/mfstock-generate-qr-code?data=" + sticker.getCode() + "\"/>";
        stickerHtml += "\t<div class=\"sticker-data employee-pass\">" + sticker.getPass() + "</div>";
        stickerHtml += "</div>";

        return stickerHtml.getBytes(StandardCharsets.UTF_8);
    }
}
