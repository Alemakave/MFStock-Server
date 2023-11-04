package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.alemakave.mfstock.configs.service.MFStockConfigLoader;
import ru.alemakave.mfstock.model.generators.NomSerStickerGenerator;
import ru.alemakave.mfstock.model.generators.NomStickerGenerator;
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
    private static final Logger log = LoggerFactory.getLogger(StickerServiceImpl.class);
    private final MFStockConfigLoader configLoader;
    private final ConfigurableApplicationContext context;

    public StickerServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext) {
        this.configLoader = configLoader;
        this.context = configurableApplicationContext;
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
}
