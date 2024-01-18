package ru.alemakave.mfstock.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.alemakave.mfstock.model.configs.DBConfigs;
import ru.alemakave.mfstock.model.configs.MFStockConfig;
import ru.alemakave.mfstock.model.configs.TelegramBotConfigs;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class MFStockConfigLoader {
    public static final String PROPERTIES_FILE_PATH = "./MFStockServer.json";
    private final Map<String, Object> configs = new HashMap<>();
    private MFStockConfig mfStockConfig;
    private TelegramBotConfigs telegramBotConfigs;

    public MFStockConfigLoader(ConfigurableApplicationContext configurableApplicationContext, MFStockConfig mfStockConfig, TelegramBotConfigs telegramBotConfigs) {
        this.mfStockConfig = mfStockConfig;
        this.telegramBotConfigs = telegramBotConfigs;
        putConfig(mfStockConfig);
        putConfig(telegramBotConfigs);
        checkFileAndCreateIfNotFound();
        load(configurableApplicationContext);
    }

    private void checkFileAndCreateIfNotFound() {
        File propertiesFile = new File(PROPERTIES_FILE_PATH);
        if (!propertiesFile.exists()) {
            ((MFStockConfig)configs.get(mfStockConfig.getClass().getName())).setDBConfigs(new DBConfigs());
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode resultNode = objectMapper.createObjectNode();

                for (Object object : configs.values()) {
                    ObjectNode objectNode = objectMapper.valueToTree(object);
                    objectNode.fields().forEachRemaining(
                            stringJsonNodeEntry -> resultNode.set(stringJsonNodeEntry.getKey(), stringJsonNodeEntry.getValue())
                    );
                }


                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(new File(PROPERTIES_FILE_PATH), resultNode);
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
            TelegramBotConfigs telegramBotConfigs = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(resource.getInputStream(), TelegramBotConfigs.class);

            this.mfStockConfig.setDBConfigs(mfStockConfig.getDBConfigs());
            this.mfStockConfig.setPrinterName(mfStockConfig.getPrinterName());

            this.telegramBotConfigs.setBotName(telegramBotConfigs.getBotName());
            this.telegramBotConfigs.setBotToken(telegramBotConfigs.getBotToken());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void putConfig(Object config) {
        configs.put(config.getClass().getName(), config);
    }

    public MFStockConfig getMfStockConfig() {
        return mfStockConfig;
    }

    public TelegramBotConfigs getTelegramBotConfigs() {
        return telegramBotConfigs;
    }
}
