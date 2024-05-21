package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
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
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.databind.deserializer.*;
import ru.alemakave.mfstock.exceptions.StickerTableException;
import ru.alemakave.mfstock.generators.*;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.sticker.*;
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
public class StickerGeneratorServiceImpl implements IStickerService {
    public static final String PAGE_NOM_STICKER_RESOURCE_LOCATION = "classpath:/pages/MFStockNomStickerGenerator.html";
    public static final String PAGE_NOM_SER_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockNomSerStickerGenerator.html";
    public static final String PAGE_CELL_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockCellStickerGenerator.html";
    public static final String PAGE_EMPLOYEE_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockEmployeeStickerGenerator.html";
    public static final String PAGE_ORDER_NUMBER_STICKER_RESOURCE_LOCATION = "classpath:pages/MFStockOrderNumberStickerGenerator.html";
    public static final String HOME_PAGE_RESOURCE_LOCATION = "classpath:pages/MFStockHome.html";
    private static final Logger log = LoggerFactory.getLogger(StickerGeneratorServiceImpl.class);
    private final MFStockConfigLoader configLoader;
    private final ConfigurableApplicationContext context;

    public StickerGeneratorServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext) {
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
        final ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, new NomPrintStickerDeserializer());
        mapper.registerModule(module);

        try {
            final PrintStickerJson<NomSticker> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);
            final NomSticker dta = printStickerJson.getSticker();
            final File stickerTempFile = new File(stickerTempFileName);
            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }
            NomStickerGenerator nomStickerGenerator = new NomStickerGenerator(context);
            nomStickerGenerator.generate(stickerTempFile, dta.getCode(), dta.getName());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
            printConfiguration.setCopies(Integer.parseInt(dta.getCopies()));
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        }

        return requestBody;
    }

    @Override
    public String postNomSerStickerGenerator(String requestBody) {
        log.info(String.format("postNomSerStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "nom_ser_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, new NomSerPrintStickerDeserializer());
        mapper.registerModule(module);

        try {
            final PrintStickerJson<NomSerSticker> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);
            final NomSerSticker dta = printStickerJson.getSticker();
            final File stickerTempFile = new File(stickerTempFileName);
            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }
            NomSerStickerGenerator nomSerStickerGenerator = new NomSerStickerGenerator(context);
            nomSerStickerGenerator.generate(stickerTempFile, dta.getCode(), dta.getName(), dta.getSerial());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
            printConfiguration.setCopies(Integer.parseInt(dta.getCopies()));
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public String postCellStickerGenerator(String requestBody) {
        log.info(String.format("postCellStickerGenerator(%s)", requestBody));
        final String stickerTempFileName = "cell_sticker.xlt";
        final ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, new CellPrintStickerDeserializer());
        mapper.registerModule(module);
        try {
            final PrintStickerJson<CellSticker> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);
            final CellSticker dta = printStickerJson.getSticker();
            final File stickerTempFile = new File(stickerTempFileName);
            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }
            CellStickerGenerator nomSerStickerGenerator = new CellStickerGenerator(context);
            nomSerStickerGenerator.generate(stickerTempFile, dta.getCellAddress(), dta.getCellCode());
            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
            printConfiguration.setCopies(1);
            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
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
        final ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, new EmployeePrintStickerDeserializer());
        mapper.registerModule(module);

        try {
            final PrintStickerJson<EmployeeSticker> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);
            final File stickerTempFile = new File(stickerTempFileName);
            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }
            EmployeeStickerGenerator employeeStickerGenerator = new EmployeeStickerGenerator(context);
            employeeStickerGenerator.generate(stickerTempFile, printStickerJson.getSticker());

            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
            printConfiguration.setCopies(1);

            PrintUtils.printFile(stickerTempFile, printConfiguration);
            //noinspection ResultOfMethodCallIgnored
            stickerTempFile.delete();
        } catch (IOException | PrintException | WriterException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
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
        final ObjectMapper mapper = new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(PrintStickerJson.class, new OrderPrintStickerDeserializer());
        mapper.registerModule(module);
        try {
            final PrintStickerJson<OrderNumberSticker> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);
            final OrderNumberSticker stickerData = printStickerJson.getSticker();
            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }
            if (stickerData.orderCountCargoSpaces == 0) {
                final File stickerTempFile = new File(stickerTempFileName);
                OrderNumberStickerGenerator orderNumberStickerGenerator = new OrderNumberStickerGenerator(context);
                orderNumberStickerGenerator.generate(stickerTempFile, stickerData, 0);
                ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
                printConfiguration.setCopies(1);
                PrintUtils.printFile(stickerTempFile, printConfiguration);
                //noinspection ResultOfMethodCallIgnored
                stickerTempFile.delete();
            } else {
                for (int i = 0; i < stickerData.orderCountCargoSpaces; i++) {
                    final File stickerTempFile = new File(stickerTempFileName);
                    OrderNumberStickerGenerator orderNumberStickerGenerator = new OrderNumberStickerGenerator(context);
                    orderNumberStickerGenerator.generate(stickerTempFile, stickerData, i + 1);
                    ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
                    printConfiguration.setCopies(1);
                    PrintUtils.printFile(stickerTempFile, printConfiguration);
                    //noinspection ResultOfMethodCallIgnored
                    stickerTempFile.delete();
                }
            }
        } catch (IOException | PrintException | WriterException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        }
        return requestBody;
    }

    @Override
    public ResponseEntity<String> getAvailablePrinters() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        return new ResponseEntity<>(mapper.writerWithDefaultPrettyPrinter()
                .withRootName("printers").writeValueAsString(PrintUtils.getPrintersName()), HttpStatus.OK);
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
                            (element.id().startsWith("input-nom") || element.id().startsWith("input-cell"))
                         && (element.hasAttr("placeholder"))
                    )
                    .collect(Collectors.toList());
            for (Element element : inputs) {
                if (element.hasAttr("placeholder")) {
                    columns.add(new DBConfigsColumns(element.attr("placeholder"), ""));
                }
            }

            table.saveColumnsAccordingHeaders(columns);
            jsoupDocument.getElementById("upload").append(String.format("<div style=\"\n" +
                    "    background: #aaa;\n" +
                    "    margin: 5px;\n" +
                    "    margin-left: 20px;\n" +
                    "    text-align: center;\n" +
                    "    margin-right: 25px;\n" +
                    "    font-size: 25px;\n" +
                    "\">%s</div>", file.getOriginalFilename()));
            jsoupDocument.getElementById("upload").append(table.applyAsHtml());

            if (table.getRows().get(0).getCells().size() != inputs.size()) {
                throw new Exception("Неверно заполнена таблица. Просьба проверить и перезагрузить таблицу. Найдены следующие колонки " + Arrays.toString(table.getRows().get(0).getCells().toArray()) + " ожидалось " +
                        ArrayUtils.toString(inputs.toArray(), (Function<String, Element>) element -> element.attr("placeholder"))); //TODO: Вынести в отдельный класс ошибку
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-header")) {
                tableRow.child(0).before("<div class=\"table-header-cell\"><input type=\"checkbox\" class=\"selectAllInColumn\"/></div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div class=\"table-header-cell\">Кол-во на печать</div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div id=\"table-close-button\" onclick=\"closeTable()\"><img src=\"/get-image?name=close.svg\" id=\"close-image\"\"></div>");
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-row")) {
                tableRow.child(0).before("<div class=\"table-cell\"><input type=\"checkbox\" class=\"selectRow\"/></div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div class=\"table-cell\"><input class=\"table-print-count\" type=\"number\" min=\"0\" value=\"1\" max=\"9999\" maxlength=\"4\" style=\"\n" +
                        "    width: -webkit-fill-available;\n" +
                        "    width: -moz-available;\n" +
                        "    width: fill-available;\n" +
                        "\"></div>");
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
