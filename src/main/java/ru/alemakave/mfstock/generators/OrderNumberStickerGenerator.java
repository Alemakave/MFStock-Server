package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.OrderNumberSticker;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OrderNumberStickerGenerator extends StickerGenerator {
    public OrderNumberStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_ORDER_NUMBER_STICKER).getInputStream());
    }

    public void generate(File outputFile, OrderNumberSticker sticker, int stickerNumber) throws IOException, WriterException {
        String orderNumber = sticker.getOrderNumber();
        int orderCountCargoSpaces = sticker.getOrderCountCargoSpaces();

        Map<Object, Object> dataMap = new HashMap<>();

        dataMap.put(new CellAddress(2, 1), orderNumber);

        if (stickerNumber == 0) {
            dataMap.put(new CellAddress(3, 1), "");
        } else {
            dataMap.put(new CellAddress(3, 1), String.format("%s/%s", stickerNumber, orderCountCargoSpaces));
        }

        super.generate(outputFile, dataMap);
    }
}
