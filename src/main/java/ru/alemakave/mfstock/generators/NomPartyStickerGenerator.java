package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.sticker.NomPartySticker;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class NomPartyStickerGenerator extends StickerGenerator {
    public NomPartyStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_NOM_PARTY_STICKER).getInputStream());
    }

    public void generate(File outputFile, NomPartySticker sticker) throws IOException, WriterException {
        String nomCode = sticker.getCode();
        String nomName = sticker.getName();
        String party = sticker.getParty();

        Map<Object, Object> dataMap = new HashMap<>();

        dataMap.put(new CellRangeAddress(1, 5, 1, 2), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(nomCode), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 3), nomName);
        dataMap.put(new CellAddress(2, 3), nomCode);
        dataMap.put(new CellAddress(3, 3), "Партия: " + party);


        super.generate(outputFile, dataMap);
    }
}
