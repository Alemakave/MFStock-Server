package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CellStickerGenerator extends StickerGenerator {
    public CellStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_CELL_STICKER).getInputStream());
    }

    public void generate(File outputFile, String cellAddress, String cellCode) throws IOException, WriterException {
        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 3, 4), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(cellCode), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 3), cellAddress);
//        dataMap.put(new CellAddress(4, 3), cellCode);

        super.generate(outputFile, dataMap);
    }
}
