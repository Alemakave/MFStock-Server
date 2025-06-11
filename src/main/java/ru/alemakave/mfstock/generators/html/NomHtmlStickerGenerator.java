package ru.alemakave.mfstock.generators.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.alemakave.mfstock.model.json.sticker.NomSticker;

import java.nio.charset.StandardCharsets;

public class NomHtmlStickerGenerator extends HtmlStickerGeneratorBase<NomSticker> {
    @Override
    public byte[] generate(NomSticker sticker) throws JsonProcessingException {
        StringBuilder stickerHtml = new StringBuilder();

        for (int i = 0; i < sticker.getCopies(); i++) {
            stickerHtml.append("<div class=\"sticker\">");
            stickerHtml.append("\t<img class=\"sticker-barcode\"")
                    .append(String.format("src=\"/mfstock-generate-qr-code?data=%s", sticker.getCode()))
                    .append("\"/>");
            stickerHtml.append("\t<div class=\"sticker-data\">");

            stickerHtml.append("\t\t<div class=\"sticker-nom-code\">");
            stickerHtml.append("\t\t\t").append(sticker.getCode());
            stickerHtml.append("\t\t</div>");

            stickerHtml.append("\t\t<div class=\"sticker-nom-name\">");
            stickerHtml.append("\t\t\t").append(sticker.getName());
            stickerHtml.append("\t\t</div>");

            stickerHtml.append("\t</div>");
            stickerHtml.append("</div>");
        }

        return stickerHtml.toString().getBytes(StandardCharsets.UTF_8);
    }
}
