package ru.alemakave.mfstock.configs;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.alemakave.mfstock.model.configs.DBConfigs;
import ru.alemakave.mfstock.model.configs.DBConfigsColumns;
import ru.alemakave.mfstock.model.configs.MFStockConfig;
import ru.alemakave.mfstock.model.configs.UserData;

import java.io.File;
import java.io.IOException;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(scopeName = SCOPE_SINGLETON)
public class MFStockConfigLoader {
    private final String propertiesFilePath;
    @Getter
    private MFStockConfig mfStockConfig;

    public MFStockConfigLoader(ConfigurableApplicationContext configurableApplicationContext, @Value("${mfstock.config.path:./MFStockServer.json}") String propertiesFilePath) {
        this.propertiesFilePath = propertiesFilePath;
        checkFileAndCreateIfNotFound();
        load(configurableApplicationContext);
    }

    private void checkFileAndCreateIfNotFound() {
        File propertiesFile = new File(propertiesFilePath);
        if (!propertiesFile.exists()) {
            try {
                MFStockConfig newConfig = new MFStockConfig("", new DBConfigs(new DBConfigsColumns[]{new DBConfigsColumns()}), new UserData[0]);

                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValue(propertiesFile, newConfig);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void load(ConfigurableApplicationContext configurableApplicationContext) {
        Resource resource = configurableApplicationContext.getResource("file:" + propertiesFilePath);
        try {
            mfStockConfig = new ObjectMapper()
                    .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(resource.getInputStream(), MFStockConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
