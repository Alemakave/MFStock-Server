package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.generators.CellStickerGenerator;
import ru.alemakave.mfstock.generators.EmployeeStickerGenerator;
import ru.alemakave.mfstock.generators.NomSerStickerGenerator;
import ru.alemakave.mfstock.generators.NomStickerGenerator;
import ru.alemakave.mfstock.model.json.CellSticker;
import ru.alemakave.mfstock.model.json.EmployeeSticker;
import ru.alemakave.mfstock.model.json.NomSerSticker;
import ru.alemakave.mfstock.model.json.NomSticker;
import ru.alemakave.slib.PrintConfigurationBuilder;
import ru.alemakave.slib.PrintConfigurationBuilder.ExcelPrintConfiguration;
import ru.alemakave.slib.utils.PrintUtils;

import javax.print.PrintException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
public class StickerServiceImpl implements IStickerService {
    public static final String PAGE_NOM_STICKER_RESOURCE_LOCATION = "classpath:/pages/MFStockNomStickerGenerator.html";
    public static final String PAGE_NOM_SER_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockNomSerStickerGenerator.html";
    public static final String PAGE_CELL_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockCellStickerGenerator.html";
    public static final String PAGE_EMPLOYEE_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockEmployeeStickerGenerator.html";
    public static final String HOME_PAGE_RESOURCE_LOCATION = "classpath:pages/MFStockHome.html";
    private static final Logger log = LoggerFactory.getLogger(StickerServiceImpl.class);
    private final MFStockConfigLoader configLoader;
    private final ConfigurableApplicationContext context;

    public StickerServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext) {
        this.configLoader = configLoader;
        this.context = configurableApplicationContext;
    }

    @Override
    public String getHomePage() throws IOException {
        try (InputStream pageNomSerStickerInputStream = context.getResource(HOME_PAGE_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageNomSerStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String getNomStickerGenerator() throws IOException {
        try (InputStream pageNomStickerInputStream = context.getResource(PAGE_NOM_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomStickerInputStream = new BufferedInputStream(pageNomStickerInputStream)) {
                return new String(bufferedPageNomStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String getNomSerStickerGenerator() throws IOException {
        try (InputStream pageNomSerStickerInputStream = context.getResource(PAGE_NOM_SER_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageNomSerStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String getCellStickerGenerator() throws IOException {
        try (InputStream pageNomSerStickerInputStream = context.getResource(PAGE_CELL_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageNomSerStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String postNomStickerGenerator(String requestBody) {
        log.info(String.format("postNomStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "nom_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final NomSticker dta = mapper.readValue(requestBody, NomSticker.class);
            final File stickerTempFile = new File(stickerTempFileName);
            NomStickerGenerator nomStickerGenerator = new NomStickerGenerator(context);
            nomStickerGenerator.generate(stickerTempFile, dta.getCode(), dta.getName());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
            printConfiguration.setCopies(Integer.parseInt(dta.getCopies()));
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }

        return requestBody;
    }

    @Override
    public String postNomSerStickerGenerator(String requestBody) {
        log.info(String.format("postNomSerStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "nom_ser_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final NomSerSticker dta = mapper.readValue(requestBody, NomSerSticker.class);
            final File stickerTempFile = new File(stickerTempFileName);
            NomSerStickerGenerator nomSerStickerGenerator = new NomSerStickerGenerator(context);
            nomSerStickerGenerator.generate(stickerTempFile, dta.getCode(), dta.getName(), dta.getSerial());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
            printConfiguration.setCopies(Integer.parseInt(dta.getCopies()));
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public String postCellStickerGenerator(String requestBody) {
        log.info(String.format("postCellStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "cell_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final CellSticker dta = mapper.readValue(requestBody, CellSticker.class);
            final File stickerTempFile = new File(stickerTempFileName);
            CellStickerGenerator nomSerStickerGenerator = new CellStickerGenerator(context);
            nomSerStickerGenerator.generate(stickerTempFile, dta.getCellAddress(), dta.getCellCode());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
            printConfiguration.setCopies(1);
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public String getEmployeeStickerGenerator() throws IOException {
        try (InputStream pageEmployeeStickerInputStream = context.getResource(PAGE_EMPLOYEE_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageEmployeeStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String postEmployeeStickerGenerator(String requestBody) {
        log.info(String.format("postEmployeeStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "employee_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final EmployeeSticker stickerData = mapper.readValue(requestBody, EmployeeSticker.class);
            final File stickerTempFile = new File(stickerTempFileName);
            EmployeeStickerGenerator employeeStickerGenerator = new EmployeeStickerGenerator(context);
            employeeStickerGenerator.generate(stickerTempFile, stickerData);
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
            printConfiguration.setCopies(1);
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }
}
