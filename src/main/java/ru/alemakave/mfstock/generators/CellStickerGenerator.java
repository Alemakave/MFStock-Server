package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.sticker.CellSticker;
import ru.alemakave.mfstock.model.json.sticker.Sticker;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CellStickerGenerator extends StickerGenerator {
    public CellStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_CELL_STICKER));
    }

    @Override
    public List<File> generate(File outputFile, Sticker sticker) throws IOException, WriterException {
        CellSticker cellSticker = (CellSticker)sticker;

        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 3, 4), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(cellSticker.getCellCode()), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 3), cellSticker.getCellAddress());

        super.generate(outputFile, dataMap);

        return List.of(outputFile);
    }
}
