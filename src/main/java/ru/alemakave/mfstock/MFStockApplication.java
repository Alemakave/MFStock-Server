package ru.alemakave.mfstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.alemakave.mfstock.configs.service.MFStockConfigLoader;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MFStockApplication {
    public MFStockConfigLoader props1;
    private final Logger logger = LoggerFactory.getLogger(MFStockApplication.class);

    public MFStockApplication(MFStockConfigLoader props1) {
        this.props1 = props1;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(MFStockApplication.class)
                .run(args);
    }

    @PostConstruct
    public void postInit() {
        logger.info(props1.getMfStockConfig().toString());
    }
}
