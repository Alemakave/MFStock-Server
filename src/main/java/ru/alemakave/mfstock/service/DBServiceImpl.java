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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            rows.add(getDB().getRows().get(0));

            String[] searchSubString = searchString.split("#");
            List<TableRow> findedRows = TableUtils.findRowContains(getDB().getRows(), searchSubString[0]);
            for (int i = 1; i < searchSubString.length; i++) {
                findedRows = TableUtils.findRowContains(findedRows, searchSubString[i]);
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
                databasePart.saveRowAccordingFilter(row -> !row.isEmpty());
                DBConfigsColumns[] configsColumns = configLoader.getMfStockConfig().getDBConfigs().getColumns();
                if (configsColumns != null) {
                    databasePart.saveColumnsAccordingHeaders(configsColumns);
                    databasePart.addColumnPrefix(configsColumns);
                }
                database.encourage(databasePart);
            }
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

            rows.addAll(findedRows);
        }

        if (searchString != null && !searchString.isEmpty()) {
            jsoupDocument.body().child(1).append(new Table(rows).applyAsHtml());
            jsoupDocument.body().append("<script type=\"text/javascript\" src=\"/js/qr-generator-button.js\"></script>");
        }

        return jsoupDocument.toString();
    }
}
