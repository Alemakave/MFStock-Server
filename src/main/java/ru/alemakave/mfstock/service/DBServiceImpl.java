package ru.alemakave.mfstock.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.alemakave.mfstock.telegram_bot.TelegramCachePhotoFilesManager;
import ru.alemakave.mfstock.utils.PageUtils;
import ru.alemakave.mfstock.utils.TableUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TelegramCachePhotoFilesManager telegramCachePhotoFilesManager;

    public DBServiceImpl(MFStockConfigLoader configLoader, ConfigurableApplicationContext configurableApplicationContext, TelegramCachePhotoFilesManager telegramCachePhotoFilesManager) {
        this.configLoader = configLoader;
        this.configurableApplicationContext = configurableApplicationContext;
        if (databaseFilePath == null) {
            databaseFilePath = ".\\Cache\\DB.xlsx";
        }
        this.telegramCachePhotoFilesManager = telegramCachePhotoFilesManager;
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

    @Override
    public byte[] getPhoto(String nomCode) {
        try {
            int index = 1;
            File photo = telegramCachePhotoFilesManager.getPhotoFile(nomCode + "_" + index);
            List<byte[]> photosBytes = new ArrayList<>();
            while (photo != null) {
                FileInputStream fis = new FileInputStream(photo);
                byte[] photoBytes = fis.readAllBytes();
                fis.close();
                photosBytes.add(photoBytes);
                index++;
                photo = telegramCachePhotoFilesManager.getPhotoFile(nomCode + "_" + index);
            }

            if (photosBytes.isEmpty()) {
                return null;
            }

            return photosBytes.get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
            calculateCellValue();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void calculateCellValue() {
        List<TableRow> rows = database.getRows();

        int nomCodeIndex = rows.get(0).getCells().indexOf(new TableCell("Номенклатурный код"));  //0;
        int nomSerIndex = rows.get(0).getCells().indexOf(new TableCell("Серийный Номер Изг."));  //3;
        int nomCountIndex = rows.get(0).getCells().indexOf(new TableCell("Кол-во"));             //4;
        int nomCellAddressIndex = rows.get(0).getCells().indexOf(new TableCell("Номер ячейки")); //6;

        for (int i = 0; i < rows.size(); i++) {
            for (int j = i+1; j < rows.size(); j++) {
                List<TableCell> cells1 = rows.get(i).getCells();
                List<TableCell> cells2 = rows.get(j).getCells();
                if (cells1 == null || cells2 == null) {
                    continue;
                }

                if (cells1.get(nomCodeIndex).equals(cells2.get(nomCodeIndex))
                        && cells1.get(nomSerIndex).equals(cells2.get(nomSerIndex))
                        && cells1.get(nomCellAddressIndex).equals(cells2.get(nomCellAddressIndex))) {
                    TableCell countCell = cells1.get(nomCountIndex);
                    countCell.setValue(Integer.toString(Integer.parseInt(countCell.getValue()) + Integer.parseInt(cells2.get(nomCountIndex).getValue())));
                    rows.get(j).setCells(null);
                }
            }
        }

        database.setRows(rows.stream()
                .filter(tableRow -> tableRow.getCells() != null)
                .collect(Collectors.toList())
        );
    }

    @PostConstruct
    public void init() {
        loadDB();
    }
}
