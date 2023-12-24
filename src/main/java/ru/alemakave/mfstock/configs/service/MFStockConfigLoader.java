package ru.alemakave.mfstock.configs.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.alemakave.mfstock.configs.model.DBConfigs;
import ru.alemakave.mfstock.configs.model.MFStockConfig;
import ru.alemakave.mfstock.configs.model.TelergamBotConfigs;

import java.io.File;
import java.io.IOException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class MFStockConfigLoader {
    public static final String PROPERTIES_FILE_PATH = "./MFStockServer.json";
    private MFStockConfig mfStockConfig;
    private TelergamBotConfigs telegramBotConfigs;

    public MFStockConfigLoader(ConfigurableApplicationContext configurableApplicationContext, MFStockConfig mfStockConfig, TelergamBotConfigs telegramBotConfigs) {
        this.mfStockConfig = mfStockConfig;
        this.telegramBotConfigs = telegramBotConfigs;
        checkFileAndCreateIfNotFound();
        load(configurableApplicationContext);
    }

    private void checkFileAndCreateIfNotFound() {
        File propertiesFile = new File(PROPERTIES_FILE_PATH);
        if (!propertiesFile.exists()) {
            mfStockConfig.setDBConfigs(new DBConfigs());
            try {
                new ObjectMapper()
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(new File(PROPERTIES_FILE_PATH), mfStockConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void load(ConfigurableApplicationContext configurableApplicationContext) {
        Resource resource = configurableApplicationContext.getResource("file:" + PROPERTIES_FILE_PATH);
        try {
            MFStockConfig mfStockConfig = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(resource.getInputStream(), MFStockConfig.class);
            TelergamBotConfigs telegramBotConfigs = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(resource.getInputStream(), TelergamBotConfigs.class);

            this.mfStockConfig.setDBConfigs(mfStockConfig.getDBConfigs());
            this.mfStockConfig.setPrinterName(mfStockConfig.getPrinterName());

            this.telegramBotConfigs.setBotName(telegramBotConfigs.getBotName());
            this.telegramBotConfigs.setBotToken(telegramBotConfigs.getBotToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public MFStockConfig getMfStockConfig() {
        return mfStockConfig;
    }

    public TelergamBotConfigs getTelegramBotConfigs() {
        return telegramBotConfigs;
    }
}
