package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.CellType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.exceptions.DBException;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.table.Table;
import ru.alemakave.mfstock.model.table.TableCell;
import ru.alemakave.mfstock.model.table.TableRow;
import ru.alemakave.mfstock.utils.PageUtils;
import ru.alemakave.mfstock.utils.TableUtils;
import ru.alemakave.slib.utils.FileUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DBServiceImpl implements IDBService {
    private static final String CLOSE_DB_PAGE_RESOURCE_PATH = "classpath:/pages/MFStockCloseDBStatus.html";
    private static final String LOAD_DB_PAGE_RESOURCE_PATH = "classpath:/pages/MFStockLoadDBStatus.html";
    private static final String FIND_PAGE_RESOURCE_PATH = "classpath:/pages/MFStockFindPage.html";

    private final Logger logger = LoggerFactory.getLogger(DBServiceImpl.class);

    private final List<String> databaseFilesPath;

    private Table database = null;
    private final MFStockConfigLoader configLoader;
    private final ConfigurableApplicationContext configurableApplicationContext;

    public DBServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext, @Value("${mfstock.database.path}") String databaseFilesPath) {
        this.configLoader = configLoader;
        this.configurableApplicationContext = configurableApplicationContext;
        this.databaseFilesPath = List.of(databaseFilesPath.split(File.pathSeparator));
    }

    @Override
    public Object getDBDate() {
        return getDB().getDatabaseDate();
    }

    @Override
    public String closeDB() {
        database = null;
        System.gc();

        return PageUtils.getPage(configurableApplicationContext.getResource(CLOSE_DB_PAGE_RESOURCE_PATH));
    }

    @Override
    public String reloadDB() {
        loadDB();
        return PageUtils.getPage(configurableApplicationContext.getResource(LOAD_DB_PAGE_RESOURCE_PATH));
    }

    @Override
    public String findFromScan(String searchString) {
        try {
            if (searchString == null || searchString.isEmpty()) {
                return new ObjectMapper().writerWithDefaultPrettyPrinter().withRootName("rows").writeValueAsString(null);
            }
            searchString = searchString.strip();
            logger.info("Search string: " + searchString);
            List<TableRow> rows = new ArrayList<>();
            rows.add(getDB().getHeader());

            String[] searchSubString = searchString.split("#");
            List<TableRow> findedRows = TableUtils.findRowContains(getDB().getRows(), searchSubString[0]);
            for (int i = 1; i < searchSubString.length; i++) {
                findedRows = TableUtils.findRowContains(findedRows, searchSubString[i]);
            }

            int serialNumberColumnIndex = rows.get(0).getCells().indexOf(new TableCell("Серийный Номер Изг.", CellType.STRING));
            int cellNumberColumnIndex = rows.get(0).getCells().indexOf(new TableCell("Номер ячейки", CellType.STRING));

            for (int i = 1; i < findedRows.size(); i++) {
                if (findedRows.get(i).getCell(serialNumberColumnIndex).equals(findedRows.get(i - 1).getCell(serialNumberColumnIndex))) {
                    if (findedRows.get(i).getCell(cellNumberColumnIndex).isEmpty()) {
                        findedRows.set(i, new TableRow());
                    } else if (findedRows.get(i - 1).getCell(cellNumberColumnIndex).isEmpty()) {
                        findedRows.set(i - 1, new TableRow());
                    } else {
                        logger.error(String.format("Не удалось удалить дубликат \"%s\"", findedRows));
                    }
                }
            }

            rows.addAll(findedRows);

            Table foundedTable = new Table(rows);

            return new ObjectMapper().writerWithDefaultPrettyPrinter().withRootName("rows").writeValueAsString(foundedTable.getRows());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Table getDB() {
        if (database == null) {
            throw new DBException("Database not loaded.");
        }

        return database;
    }

    @PostConstruct
    private void loadDB() {
        try {
            if (databaseFilesPath == null) {
                logger.error("Database file path not set! Set database file path parameter \"--mfstock.database.path=[file path]\" and rerun application.");
                return;
            }

            database = new Table(new ArrayList<>());
            TableRow headerRow = new TableRow();
            for (DBConfigsColumns column : configLoader.getMfStockConfig().getDBConfigs().getColumns()) {
                headerRow.addCell(new TableCell(column.getHeaderText(), CellType.STRING));
            }
            database.addRows(headerRow);
            for (String databaseFilePath : databaseFilesPath) {
                logger.info(String.format("Loading \"%s\"", databaseFilePath));
                Table databasePart = new Table(new File(databaseFilePath));
                if (databasePart.getDatabaseDate().compareTo(database.getDatabaseDate()) > 0) {
                    database.setDatabaseDate(databasePart.getDatabaseDate());
                }
                databasePart.saveRowAccordingFilter(row -> !row.isEmpty());
                DBConfigsColumns[] configsColumns = configLoader.getMfStockConfig().getDBConfigs().getColumns();
                if (configsColumns != null) {
                    databasePart.saveColumnsAccordingHeaders(configsColumns);
                    databasePart.addColumnPrefix(configsColumns);
                }
                database.encourage(databasePart);
            }

            try (Stream<Path> filePaths = Files.list(Path.of("C:\\MFStock\\Cache\\Отгруженно"))) {
                List<File> files = filePaths.filter(path -> path.getFileName().toString().startsWith("8200") || path.getFileName().toString().startsWith("8300"))
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                for (File file : files) {
                    boolean skipLoad = false;
                    logger.info(String.format("Loading \"%s\"", file.getName()));
                    Table databasePart = new Table(file);
                    if (databasePart.getDatabaseDate().compareTo(database.getDatabaseDate()) > 0) {
                        database.setDatabaseDate(databasePart.getDatabaseDate());
                    }
                    databasePart.saveRowAccordingFilter(row -> !row.isEmpty());
                    TableRow databasePartHeaderRow = databasePart.getHeader();

                    int nomCodeColumnIndex = databasePartHeaderRow.getCells().indexOf(new TableCell("Код", CellType.STRING));
                    if (nomCodeColumnIndex != -1) {
                        databasePartHeaderRow.getCell(nomCodeColumnIndex)
                                .setValue("Номенклатурный код");
                    } else {
                        logger.error(String.format("Не удалось загрузить номенклатурный код из файла \"%s\". Файл был пропущен.", file.getName()));
                        skipLoad = true;
                    }

                    int countColumnIndex = databasePartHeaderRow.getCells().indexOf(new TableCell("Подобрано", CellType.STRING));
                    if (countColumnIndex != -1) {
                        databasePartHeaderRow.getCell(countColumnIndex)
                                .setValue("Кол-во");
                    } else {
                        logger.error(String.format("Не удалось загрузить кол-во из файла \"%s\". Файл был пропущен.", file.getName()));
                        skipLoad = true;
                    }

                    int palletNumberColumnIndex = databasePartHeaderRow.getCells().indexOf(new TableCell("Паллета", CellType.STRING));
                    if (palletNumberColumnIndex != -1) {
                        databasePartHeaderRow.getCell(palletNumberColumnIndex)
                                .setValue("Номер паллеты");
                    } else {
                        logger.error(String.format("Не удалось загрузить номер паллеты из файла \"%s\". Файл был пропущен.", file.getName()));
                        skipLoad = true;
                    }

                    if (skipLoad) {
                        continue;
                    }

                    int sapNumberColumnIndex = databasePartHeaderRow.getCells().indexOf(new TableCell("Приоритет", CellType.STRING));
                    if (sapNumberColumnIndex != -1) {
                        databasePartHeaderRow.getCell(sapNumberColumnIndex)
                                .setValue("ДокSAP");
                        databasePart.addColumnPrefix(new DBConfigsColumns("ДокSAP", "Отгружен по документу " + FileUtils.getFileNameWithoutExtension(file)));
                    } else {
                        logger.warn(String.format("Не удалось найти колонку с текстом \"%s\" в файле \"%s\" для заполнения ДокSAP.", "Приоритет", file.getName()));
                    }

                    databasePart.saveRowAccordingFilter(tableRow -> !tableRow.getCell(nomCodeColumnIndex).getValue().startsWith("10030"));

                    DBConfigsColumns[] configsColumns = configLoader.getMfStockConfig().getDBConfigs().getColumns();
                    if (configsColumns != null) {
                        databasePart.saveColumnsAccordingHeaders(configsColumns);
                        databasePart.addColumnPrefix(configsColumns);
                    }
                    database.encourage(databasePart);
                }
            }
/*
            int nomCodeColumnIndex = database.getHeader().getCells().indexOf(new TableCell("Номенклатурный код", CellType.STRING));
            int serialNumberColumnIndex = database.getHeader().getCells().indexOf(new TableCell("Серийный Номер Изг.", CellType.STRING));
            int cellNumber = database.getHeader().getCells().indexOf(new TableCell("Номер ячейки", CellType.STRING));
            database.setRows(database.getRows()
                    .stream()
                    .sorted(Comparator.comparing(tableRow -> tableRow.getCell(serialNumberColumnIndex).getValue()))
                    .collect(Collectors.toList()));

            for (int i = 1; i < database.getRowsCount(); i++) {
                if (database.getRow(i).getCell(nomCodeColumnIndex).getValue().startsWith("10030")) {
                    continue;
                }

                if (database.getRow(i).getCell(serialNumberColumnIndex).isEmpty()) {
                    continue;
                }

                TableRow currentRow = database.getRow(i);
                TableRow prevRow = database.getRow(i-1);

                if (currentRow.getCell(serialNumberColumnIndex).equals(prevRow.getCell(serialNumberColumnIndex))) {
                    if (currentRow.getCell(cellNumber).isEmpty()) {
                        currentRow.setCells(new ArrayList<>());
                        currentRow.setCell(database.getHeader().getCells().size() - 1, TableCell.EMPTY);
                    } else if (prevRow.getCell(cellNumber).isEmpty()) {
                        prevRow.setCells(new ArrayList<>());
                        prevRow.setCell(database.getHeader().getCells().size() - 1, TableCell.EMPTY);
                    } else {
                        logger.warn(String.format("Не удалось удалить дубликат по СНИ \"%s\" для \"%s\"", currentRow.getCell(serialNumberColumnIndex), currentRow.getCell(nomCodeColumnIndex)));
                    }
                }
            }
*/
            logger.info("DB loaded.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String find(String searchString) {
        Document jsoupDocument = Jsoup.parse(PageUtils.getPage(configurableApplicationContext.getResource(FIND_PAGE_RESOURCE_PATH)));

        List<TableRow> rows = new ArrayList<>();
        if (searchString != null && !searchString.isEmpty()) {
            searchString = searchString.strip();
            logger.info("Search string: " + searchString);
            rows.add(getDB().getRows().get(0));

            String[] searchSubString = searchString.split("#");
            List<TableRow> findedRows = TableUtils.findRowContains(getDB().getRows(), searchSubString[0]);
            for (int i = 1; i < searchSubString.length; i++) {
                findedRows = TableUtils.findRowContains(findedRows, searchSubString[i]);
            }

            int serialNumberColumnIndex = rows.get(0).getCells().indexOf(new TableCell("Серийный Номер Изг.", CellType.STRING));
            int cellNumberColumnIndex = rows.get(0).getCells().indexOf(new TableCell("Номер ячейки", CellType.STRING));

            for (int i = 1; i < findedRows.size(); i++) {
                if (findedRows.get(i).getCell(serialNumberColumnIndex).getValue().isEmpty()) {
                    continue;
                }
                if (findedRows.get(i-1).getCell(serialNumberColumnIndex).getValue().isEmpty()) {
                    continue;
                }

                if (findedRows.get(i).getCell(serialNumberColumnIndex).equals(findedRows.get(i - 1).getCell(serialNumberColumnIndex))) {
                    if (findedRows.get(i).getCell(cellNumberColumnIndex).isEmpty()) {
                        findedRows.set(i, new TableRow());
                    } else if (findedRows.get(i - 1).getCell(cellNumberColumnIndex).isEmpty()) {
                        findedRows.set(i - 1, new TableRow());
                    } else {
                        logger.error(String.format("Не удалось удалить дубликат \"%s\"", findedRows));
                    }
                }
            }

            rows.addAll(findedRows);
        }

        if (searchString != null && !searchString.isEmpty()) {
            jsoupDocument.body().child(1).append(new Table(rows).applyAsHtml());
            jsoupDocument.body().append("<script type=\"text/javascript\" src=\"/js/qr-generator-button.js\"></script>");
        }

        return jsoupDocument.toString();
    }
}
