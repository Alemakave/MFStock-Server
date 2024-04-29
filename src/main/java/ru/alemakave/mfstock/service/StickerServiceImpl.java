package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.exceptions.StickerTableException;
import ru.alemakave.mfstock.generators.*;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.json.*;
import ru.alemakave.mfstock.model.table.Table;
import ru.alemakave.mfstock.model.table.TableCell;
import ru.alemakave.mfstock.model.table.TableRow;
import ru.alemakave.slib.PrintConfigurationBuilder;
import ru.alemakave.slib.PrintConfigurationBuilder.ExcelPrintConfiguration;
import ru.alemakave.slib.utils.ArrayUtils;
import ru.alemakave.slib.utils.PrintUtils;
import ru.alemakave.slib.utils.function.Function;

import javax.print.PrintException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StickerServiceImpl implements IStickerService {
    public static final String PAGE_NOM_STICKER_RESOURCE_LOCATION = "classpath:/pages/MFStockNomStickerGenerator.html";
    public static final String PAGE_NOM_SER_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockNomSerStickerGenerator.html";
    public static final String PAGE_CELL_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockCellStickerGenerator.html";
    public static final String PAGE_PARTY_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockNomPartyStickerGenerator.html";
    public static final String PAGE_EMPLOYEE_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockEmployeeStickerGenerator.html";
    public static final String PAGE_ORDER_NUMBER_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockOrderNumberStickerGenerator.html";
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

    @Override
    public String getNomPartyStickerGenerator() throws IOException {
        try (InputStream pageEmployeeStickerInputStream = context.getResource(PAGE_PARTY_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageEmployeeStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String postNomPartyStickerGenerator(@RequestBody  String requestBody) {
        log.info(String.format("postNomPartyStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "party_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final NomPartySticker stickerData = mapper.readValue(requestBody, NomPartySticker.class);
            final File stickerTempFile = new File(stickerTempFileName);
            NomPartyStickerGenerator nomPartyStickerGenerator = new NomPartyStickerGenerator(context);
            nomPartyStickerGenerator.generate(stickerTempFile, stickerData);
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
            printConfiguration.setCopies(Integer.parseInt(stickerData.getCopies()));
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public String getOrderNumberStickerGenerator() throws IOException {
        try (InputStream pageEmployeeStickerInputStream = context.getResource(PAGE_ORDER_NUMBER_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageEmployeeStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
    }

    @Override
    public String postOrderNumberStickerGenerator(String requestBody) {
        log.info(String.format("postOrderNumberStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "order_number_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper();
        try {
            final OrderNumberSticker stickerData = mapper.readValue(requestBody, OrderNumberSticker.class);
            if (stickerData.orderCountCargoSpaces == 0) {
                final File stickerTempFile = new File(stickerTempFileName);
                OrderNumberStickerGenerator orderNumberStickerGenerator = new OrderNumberStickerGenerator(context);
                orderNumberStickerGenerator.generate(stickerTempFile, stickerData, 0);
                ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
                printConfiguration.setCopies(1);
                PrintUtils.printFile(stickerTempFile, printConfiguration);
                //noinspection ResultOfMethodCallIgnored
                stickerTempFile.delete();
            } else {
                for (int i = 0; i < stickerData.orderCountCargoSpaces; i++) {
                    final File stickerTempFile = new File(stickerTempFileName);
                    OrderNumberStickerGenerator orderNumberStickerGenerator = new OrderNumberStickerGenerator(context);
                    orderNumberStickerGenerator.generate(stickerTempFile, stickerData, i + 1);
                    ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(configLoader.getMfStockConfig().getPrinterName());
                    printConfiguration.setCopies(1);
                    PrintUtils.printFile(stickerTempFile, printConfiguration);
                    //noinspection ResultOfMethodCallIgnored
                    stickerTempFile.delete();
                }
            }
        } catch (IOException | PrintException | WriterException e) {
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public ResponseEntity<String> uploadStickersDataTable(MultipartFile file, String currentHtmlCode) {
        try {
            if (file.isEmpty()) {
                throw new FileUploadException("Файл таблицы не выбран");
            }

            BufferedInputStream bis = new BufferedInputStream(file.getResource().getInputStream());

            Document jsoupDocument = Jsoup.parse(currentHtmlCode);
            jsoupDocument.head().append("<script type=\"text/javascript\" src=\"/get-script?name=TableColumnFormater.js\"></script>");
            Table table = new Table(bis);

            validateTable(table);

            table.getRows().removeIf(tableRow -> tableRow.toString().equals(StringUtils.repeat("|", tableRow.getCells().size()-1)));
            ArrayList<DBConfigsColumns> columns = new ArrayList<>();

            List<Element> inputs =  jsoupDocument.getElementsByClass("input")
                    .stream()
                    .filter(element ->
                            element.id().startsWith("input-nom") || element.id().startsWith("input-cell")
                    )
                    .collect(Collectors.toList());
            for (Element element : inputs) {
                columns.add(new DBConfigsColumns(element.attr("placeholder"), ""));
            }

            table.saveColumnsAccordingHeaders(columns);
            jsoupDocument.getElementById("upload").append(table.applyAsHtml());

            if (table.getRows().get(0).getCells().size() != inputs.size()) {
                throw new Exception("Неверно заполнена таблица. Просьба проверить и перезагрузить таблицу. Найдены следующие колонки " + Arrays.toString(table.getRows().get(0).getCells().toArray()) + " ожидалось " +
                        ArrayUtils.toString(inputs.toArray(), (Function<String, Element>) element -> element.attr("placeholder"))); //TODO: Вынести в отдельный класс ошибку
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-header")) {
                tableRow.child(0).before("<div class=\"table-header-cell\"><input type=\"checkbox\" class=\"selectAllInColumn\"/></div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div id=\"table-close-button\" onclick=\"closeTable()\">X</div>");
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-row")) {
                tableRow.child(0).before("<div class=\"table-cell\"><input type=\"checkbox\" class=\"selectRow\"/></div>");
            }

            jsoupDocument.getElementById("print-button").text("Напечатать выбранное");
            jsoupDocument.getElementById("print-button").attr("onclick", "printSelectedNomenclatures();");
            jsoupDocument.body().append("<script type=\"text/javascript\">markInputBoxWithColumnIds();</script>");
            jsoupDocument.body().append("<script type=\"text/javascript\" src=\"/get-script?name=CheckRowsEvent.js\"></script>");

            HttpHeaders headers = new HttpHeaders();
            headers.add("Connection", "close");

            return new ResponseEntity<>(jsoupDocument.toString(), headers, HttpStatus.OK);
        } catch (Exception e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }

            throw new RuntimeException(e);
        }
    }

    private void validateTable(Table table) {
        for (int i = 0; i < table.getRowsCount(); i++) {
            TableRow row = table.getRows().get(i);
            for (int j = 0; j < row.getCells().size(); j++) {
                TableCell cell = row.getCells().get(j);
                if (cell.getCellType() != CellType.STRING && cell.getCellType() != CellType.BLANK) {
                    throw new StickerTableException(String.format("Загруженная таблица содержит недопустимые типы ячеек. Просьба загрузить таблицу только с текстовым типом ячеек. Обнаружена ячейка [%s:%s] с типом \"%s\"", i, j, cell.getCellType()));
                }
            }
        }
    }
}
