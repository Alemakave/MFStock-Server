package ru.alemakave.mfstock.model.generators;

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

public class NomSerStickerGenerator extends StickerGenerator {
    public NomSerStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_NOM_SER_STICKER).getInputStream());
    }

    public void generate(File outputFile, String nomCode, String nomName, String serNumber) throws IOException, WriterException {
        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(1, 5, 1, 2), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(nomCode + "#" + serNumber), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 3), nomName);
        dataMap.put(new CellAddress(2, 3), nomCode);
        dataMap.put(new CellAddress(3, 3), nomCode + "#" + serNumber);
        dataMap.put(new CellAddress(5, 3), "");
        dataMap.put(new CellAddress(4, 3), serNumber);

        super.generate(outputFile, dataMap);
    }
}
