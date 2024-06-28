package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.exceptions.RuntimeIOException;
import ru.alemakave.mfstock.exceptions.StickerGeneratorNotRegisteredException;
import ru.alemakave.mfstock.exceptions.StickerTableException;
import ru.alemakave.mfstock.generators.*;
import ru.alemakave.mfstock.model.StickerType;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.json.PrintStickerJson;
import ru.alemakave.mfstock.model.json.StickerFileUUID;
import ru.alemakave.mfstock.model.json.sticker.*;
import ru.alemakave.mfstock.model.table.Table;
import ru.alemakave.mfstock.model.table.TableCell;
import ru.alemakave.mfstock.model.table.TableRow;
import ru.alemakave.slib.PrintConfigurationBuilder;
import ru.alemakave.slib.PrintConfigurationBuilder.ExcelPrintConfiguration;
import ru.alemakave.slib.utils.ArrayUtils;
import ru.alemakave.slib.utils.PrintUtils;
import ru.alemakave.slib.utils.function.Function;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.alemakave.mfstock.model.StickerType.*;
import static ru.alemakave.mfstock.utils.JsonMapperUtils.getStickerDeserializedMapper;

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
    private final Map<StickerType, StickerGenerator> registeredStickerGenerators = new HashMap<>();
    public final String stickerDir;
    public final String stickerFileDateTimeFormat = "yyyyMMdd-HHmmss";

    public StickerGeneratorServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext, @Value("${mfstock.sticker.generated.folder.path:./generated_sticker}") String stickerDir) {
        this.configLoader = configLoader;
        this.context = configurableApplicationContext;
        this.stickerDir = stickerDir;
        registeredStickerGenerators.put(CELL, new CellStickerGenerator(configurableApplicationContext));
        registeredStickerGenerators.put(EMPLOYEE, new EmployeeStickerGenerator(configurableApplicationContext));
        registeredStickerGenerators.put(NOM, new NomStickerGenerator(configurableApplicationContext));
        registeredStickerGenerators.put(NOM_SERIAL, new NomSerStickerGenerator(configurableApplicationContext));
        registeredStickerGenerators.put(ORDER_NUMBER, new OrderNumberStickerGenerator(configurableApplicationContext));
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
    public String getEmployeeStickerGenerator() throws IOException {
        try (InputStream pageEmployeeStickerInputStream = context.getResource(PAGE_EMPLOYEE_STICKER_RESOURCE_LOCATION).getInputStream()) {
            try (BufferedInputStream bufferedPageNomSerStickerInputStream = new BufferedInputStream(pageEmployeeStickerInputStream)) {
                return new String(bufferedPageNomSerStickerInputStream.readAllBytes());
            }
        }
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
            jsoupDocument.head().append("<script type=\"text/javascript\" src=\"/js/TableColumnFormater.js\"></script>");
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
            jsoupDocument.getElementById("upload").append(String.format("<div id=\"tableFilename\" style=\"\n" +
                    "    background: #aaa;\n" +
                    "    margin: 5px;\n" +
                    "    margin-left: 20px;\n" +
                    "    margin-right: 0px;\n" +
                    "    text-align: center;\n" +
                    "    font-size: 25px;\n" +
                    "\">%s</div>", file.getOriginalFilename()));
            jsoupDocument.getElementById("upload").append(table.applyAsHtml());
            jsoupDocument.getElementById("upload-table").attr("style", "margin-left: 20px;");

            if (table.getRows().get(0).getCells().size() != inputs.size()) {
                throw new Exception("Неверно заполнена таблица. Просьба проверить и перезагрузить таблицу. Найдены следующие колонки " + Arrays.toString(table.getRows().get(0).getCells().toArray()) + " ожидалось " +
                        ArrayUtils.toString(inputs.toArray(), (Function<String, Element>) element -> element.attr("placeholder"))); //TODO: Вынести в отдельный класс ошибку
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-header")) {
                tableRow.child(0).before("<div class=\"table-header-cell\"><input type=\"checkbox\" class=\"selectAllInColumn\"/></div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div class=\"table-header-cell\">Кол-во на печать</div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div id=\"table-close-button\" onclick=\"closeTable()\"><img src=\"/img/close.svg\" id=\"close-image\"\"></div>");
            }

            for (Element tableRow : jsoupDocument.getElementsByClass("table-row")) {
                tableRow.child(0).before("<div class=\"table-cell\"><input type=\"checkbox\" class=\"selectRow\"/></div>");
                tableRow.child(tableRow.childrenSize() - 1).after("<div class=\"table-cell\"><input class=\"table-print-count\" type=\"number\" min=\"0\" value=\"1\" max=\"9999\" maxlength=\"4\" style=\"\n" +
                        "    width: -webkit-fill-available;\n" +
                        "    width: -moz-available;\n" +
                        "    width: fill-available;\n" +
                        "\"></div>");
            }

            jsoupDocument.getElementById("sticker-buttons-block").child(0).remove();
            jsoupDocument.getElementById("sticker-buttons-block").child(0).before("<div style=\"\n" +
                    "    display: flex;\n" +
                    "    width: 100%;\n" +
                    "\">\n" +
                    "        <input type=\"checkbox\" name=\"printFilenameAfterPrintStickers\" id=\"printFilenameAfterPrintStickers\">\n" +
                    "        <label for=\"printFilenameAfterPrintStickers\" class=\"label\" style=\"height: 40px;line-height: 40px;margin: 0;\">Напечатать после наклейку с именем файла</label>\n" +
                    "    </div>");

            jsoupDocument.getElementById("print-button").text("Напечатать выбранное");
            jsoupDocument.getElementById("print-button").attr("onclick", "printSelectedNomenclatures();");
            jsoupDocument.body().append("<script type=\"text/javascript\">markInputBoxWithColumnIds();</script>");
            jsoupDocument.body().append("<script type=\"text/javascript\" src=\"/js/CheckRowsEvent.js\"></script>");

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

    @Override
    public ResponseEntity<byte[]> getStickerFile(String uuidStr) {
        UUID uuid = UUID.fromString(uuidStr);

        File file = new File(stickerDir, uuid + ".xlt");
        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            return ResponseEntity.ok(fis.readAllBytes());
        } catch (IOException exception) {
            log.error(String.format("%s: %s: %s", exception.getClass().getName(), getClass().getName(), exception.getMessage()));
            for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(exception);
        }
    }

    @Override
    public ResponseEntity<List<String>> postGenerateStickerExcelFile(String requestBody, StickerType stickerType) {
        checkStickerFolder();
        final ObjectMapper mapper = getStickerDeserializedMapper(stickerType.getPrintStickerDeserializer());
        try {
            final PrintStickerJson<?> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);

            Sticker sticker = printStickerJson.getSticker();

            StickerFileUUID fileUUID = new StickerFileUUID(UUID.nameUUIDFromBytes(requestBody.getBytes()));
            File stickerFile = new File(stickerDir, String.format("%s+%s.xlt", fileUUID.getStickerFileUUID(), DateTimeFormatter.ofPattern(stickerFileDateTimeFormat).format(LocalDateTime.now())));

            ArrayList<String> fileNames = new ArrayList<>();

            if (!registeredStickerGenerators.containsKey(stickerType)) {
                throw new StickerGeneratorNotRegisteredException("Sticker generator not registered");
            }

            registeredStickerGenerators.get(stickerType).generate(stickerFile, sticker).forEach(path -> fileNames.add(path.getName()));
            return ResponseEntity.ok(fileNames);
        } catch (Exception e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void postPrintSticker(String requestBody, StickerType stickerType) {
        log.info(String.format("postPrintSticker(%s)", requestBody));

        ResponseEntity<List<String>> responseEntity = postGenerateStickerExcelFile(requestBody, stickerType);
        final ObjectMapper mapper = getStickerDeserializedMapper(stickerType.getPrintStickerDeserializer());

        try {
            final PrintStickerJson<?> printStickerJson = mapper.readValue(requestBody, PrintStickerJson.class);

            String printerName = printStickerJson.getSelectPrinter();
            if (printerName == null || printerName.isEmpty()) {
                printerName = configLoader.getMfStockConfig().getPrinterName();
            }

            ExcelPrintConfiguration printConfiguration = PrintConfigurationBuilder.buildExcelConfiguration(printerName);
            printConfiguration.setCopies(1);

            for (String stickerFileUUID : Objects.requireNonNull(responseEntity.getBody())){
                File stickerFile = new File(stickerDir, stickerFileUUID);
                PrintUtils.printFile(stickerFile, printConfiguration);
            }

            ResponseEntity.ok().build();
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

    private void checkStickerFolder() {
        if (!Files.exists(new File(stickerDir).toPath())) {
            if (!new File(stickerDir).mkdirs()) {
                throw new RuntimeIOException("Failed to create dirs");
            }
        }

        try (Stream<Path> paths = Files.list(Path.of(stickerDir))) {
            paths.filter(path -> {
                        TemporalAccessor fileDateTime = DateTimeFormatter.ofPattern(stickerFileDateTimeFormat).parse(path.getFileName().toString().split("\\+")[1].substring(0, stickerFileDateTimeFormat.length()));
                        return LocalDateTime.from(fileDateTime).plusHours(1).isBefore(LocalDateTime.now());
                    })
                    .forEach(path -> {
                        if (!path.toFile().delete()) {
                            throw new RuntimeIOException(String.format("Failed to delete file \"%s\".", path));
                        }
                    });
        } catch (IOException e) {
            log.error(String.format("%s: %s: %s", e.getClass().getName(), getClass().getName(), e.getMessage()));
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                log.error("\t" + stackTraceElement.toString());
            }
            throw new RuntimeException(e);
        }
    }
}
