package ru.alemakave.mfstock.model.generators;

import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.model.json.EmployeeSticker;
import ru.alemakave.qr.ImageType;
import ru.alemakave.qr.generator.QRGenerator;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EmployeeStickerGenerator extends StickerGenerator {
    public EmployeeStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) throws IOException {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_EMPLOYEE_STICKER).getInputStream());
    }

    public void generate(File outputFile, EmployeeSticker stickerData) throws IOException, WriterException {
        String employeeName = stickerData.getName();
        String employeeCode = stickerData.getCode();
        String employeePass = stickerData.getPass();

        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 3, 4), ImageUtils.toByteArray(QRGenerator.generateToBufferedImage(employeeCode), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 0), employeeName);
        dataMap.put(new CellAddress(4, 3), "Пароль:" + employeePass);

        super.generate(outputFile, dataMap);
    }
}
