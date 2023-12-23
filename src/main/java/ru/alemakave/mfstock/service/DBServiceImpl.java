package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import ru.alemakave.mfstock.configs.model.DBConfigsColumns;
import ru.alemakave.mfstock.configs.service.MFStockConfigLoader;
import ru.alemakave.mfstock.exceptions.DBException;
import ru.alemakave.mfstock.model.table.Table;
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

    private final Logger logger = LoggerFactory.getLogger(DBServiceImpl.class);

    @Value("${mfstock.database.path}")
    private String databaseFilePath;

    private Table database = null;
    private final MFStockConfigLoader configLoader;
    private final ConfigurableApplicationContext configurableApplicationContext;

    public DBServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext) {
        this.configLoader = configLoader;
        this.configurableApplicationContext = configurableApplicationContext;
        if (databaseFilePath == null) {
            databaseFilePath = ".\\Cache\\DB.xlsx";
        }
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

    private void loadDB() {
        try {
            database = new Table(new File(databaseFilePath));
            database.saveRowAccordingFilter(row -> !row.isEmpty());
            DBConfigsColumns[] configsColumns = configLoader.getMfStockConfig().getDBConfigs().getColumns();
            if (configsColumns != null) {
                database.saveColumnsAccordingHeaders(configsColumns);
                database.addColumnPrefix(configsColumns);
            }
            database.postInit();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        loadDB();
    }
}
