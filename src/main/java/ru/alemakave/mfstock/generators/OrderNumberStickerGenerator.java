package ru.alemakave.mfstock.generators;

import com.google.zxing.WriterException;
import com.jacob.com.NotImplementedException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.util.CellAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.sticker.OrderNumberSticker;
import ru.alemakave.mfstock.model.json.sticker.Sticker;
import ru.alemakave.slib.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class OrderNumberStickerGenerator extends StickerGenerator {
    public OrderNumberStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_ORDER_NUMBER_STICKER));
    }

    private void generate(File outputFile, OrderNumberSticker sticker, int stickerNumber) throws IOException {
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

    @Override
    public List<File> generate(File outputFile, Sticker sticker) throws IOException, WriterException {
        if (((OrderNumberSticker)sticker).orderCountCargoSpaces == 0) {
            generate(outputFile, (OrderNumberSticker) sticker, 0);
            return List.of(outputFile);
        }

        int stickerCount = ((OrderNumberSticker)sticker).orderCountCargoSpaces;
        ArrayList<File> stickerFiles = new ArrayList<>(stickerCount);

        File stickerDir = outputFile.getParentFile();
        String stickerFileName = FileUtils.getFileNameWithoutExtension(outputFile.getName());
        String stickerFileExt = outputFile.getName().substring(stickerFileName.length()+1);

        for (int i = 0; i < stickerCount; i++) {
            File stickerFile = new File(stickerDir, stickerFileName + "_" + i + "." + stickerFileExt);
            generate(stickerFile, (OrderNumberSticker) sticker, i + 1);
            stickerFiles.add(stickerFile);
        }

        NotImplementedException exception = new NotImplementedException("Method not yet implemented");
        log.error(String.format("%s: %s: %s", exception.getClass().getName(), getClass().getName(), exception));
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            log.error("\t" + stackTraceElement.toString());
        }

        return stickerFiles;
    }
}
