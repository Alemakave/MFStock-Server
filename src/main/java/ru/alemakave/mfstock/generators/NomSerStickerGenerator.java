package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.sticker.NomSerSticker;
import ru.alemakave.mfstock.model.json.sticker.Sticker;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NomSerStickerGenerator extends StickerGenerator {
    public NomSerStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_NOM_SER_STICKER));
    }

    @Override
    public List<File> generate(File outputFile, Sticker sticker) throws IOException, WriterException {
        NomSerSticker nomSerSticker = (NomSerSticker) sticker;

        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(1, 5, 1, 2), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(nomSerSticker.getCode() + "#" + nomSerSticker.getSerial()), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 3), nomSerSticker.getName());
        dataMap.put(new CellAddress(2, 3), nomSerSticker.getCode());
        dataMap.put(new CellAddress(3, 3), nomSerSticker.getCode() + "#" + nomSerSticker.getSerial());
        dataMap.put(new CellAddress(5, 3), "");
        dataMap.put(new CellAddress(4, 3), nomSerSticker.getSerial());

        super.generate(outputFile, dataMap);

        return List.of(outputFile);
    }
}
