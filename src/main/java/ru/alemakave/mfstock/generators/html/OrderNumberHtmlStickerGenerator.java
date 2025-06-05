package ru.alemakave.mfstock.generators.html;

import com.fasterxml.jackson.core.JsonProcessingException;
import ru.alemakave.mfstock.model.json.sticker.OrderNumberSticker;

import java.nio.charset.StandardCharsets;

public class OrderNumberHtmlStickerGenerator extends HtmlStickerGeneratorBase<OrderNumberSticker> {
    @Override
    public byte[] generate(OrderNumberSticker sticker) throws JsonProcessingException {
        StringBuilder stickerHtml = new StringBuilder();

        if (sticker.getOrderCountCargoSpaces() == 0) {
            stickerHtml.append(generateSticker(sticker, 0));
        } else {
            for (int i = 0; i < sticker.getOrderCountCargoSpaces(); i++) {
                stickerHtml.append(generateSticker(sticker, i + 1));
            }
        }

        return stickerHtml.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String generateSticker(OrderNumberSticker sticker, int stickerNumber) {
        String stickerHtml = "";

        String stickerNumberInfo = "";

        if (sticker.getOrderCountCargoSpaces() > 0) {
            stickerNumberInfo = stickerNumber + "/" + sticker.getOrderCountCargoSpaces();
        }

        stickerHtml += "<div class=\"sticker order\">";
        stickerHtml += "\t<div class=\"sticker-data order-number\">" + sticker.getOrderNumber() + "</div>";
        stickerHtml += "\t<div class=\"sticker-data order-count-cargo-spaces\">" + stickerNumberInfo + "</div>";
        stickerHtml += "</div>";

        return stickerHtml;
    }
}
