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

public class NomStickerGenerator extends StickerGenerator {
    public static final String TEMPLATE_NOM_STICKER = "templates/r_NomStickerMGF.XLT";

    public NomStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_NOM_STICKER).getInputStream());
    }

    public void generate(File outputFile, String nomCode, String nomName) throws IOException, WriterException {
        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 1, 2), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(nomCode), ImageType.PNG.name()));
        dataMap.put(new CellAddress(2, 3), nomCode);
        dataMap.put(new CellAddress(3, 3), nomName);

        super.generate(outputFile, dataMap);
    }
}
