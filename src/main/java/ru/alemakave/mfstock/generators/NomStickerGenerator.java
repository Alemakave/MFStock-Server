package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.sticker.NomSticker;
import ru.alemakave.mfstock.model.json.sticker.Sticker;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NomStickerGenerator extends StickerGenerator {
    public NomStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_NOM_STICKER));
    }

    @Override
    public List<File> generate(File outputFile, Sticker sticker) throws IOException, WriterException {
        NomSticker nomSticker = (NomSticker)sticker;

        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 1, 2), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(nomSticker.getCode()), ImageType.PNG.name()));
        dataMap.put(new CellAddress(2, 3), nomSticker.getCode());
        dataMap.put(new CellAddress(3, 3), nomSticker.getName());

        super.generate(outputFile, dataMap);

        return List.of(outputFile);
    }
}
