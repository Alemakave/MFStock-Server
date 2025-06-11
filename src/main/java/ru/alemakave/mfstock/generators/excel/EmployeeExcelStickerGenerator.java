package ru.alemakave.mfstock.generators.excel;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.barcode.generator.BarcodeGenerator;
import ru.alemakave.mfstock.model.json.sticker.EmployeeSticker;
import ru.alemakave.barcode.ImageType;
import ru.alemakave.slib.utils.ImageUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeExcelStickerGenerator extends ExcelStickerGenerator<EmployeeSticker> {
    public EmployeeExcelStickerGenerator(ConfigurableApplicationContext configurableApplicationContext) {
        super(configurableApplicationContext.getResource("classpath:/" + TEMPLATE_EMPLOYEE_STICKER));
    }

    @Deprecated
    public List<File> generate(File outputFile, EmployeeSticker employeeSticker) throws IOException, WriterException {
        String employeeName = employeeSticker.getName();
        String employeeCode = employeeSticker.getCode();
        String employeePass = employeeSticker.getPass();

        Map<Object, Object> dataMap = new HashMap<>();
        dataMap.put(new CellRangeAddress(2, 4, 3, 4), ImageUtils.toByteArray(BarcodeGenerator.generateBufferedImage(BarcodeFormat.QR_CODE, employeeCode), ImageType.PNG.name()));
        dataMap.put(new CellAddress(1, 0), employeeName);
        dataMap.put(new CellAddress(4, 3), "Пароль:" + employeePass);

        super.generate(outputFile, dataMap);

        return List.of(outputFile);
    }
}
