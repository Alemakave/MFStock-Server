package ru.alemakave.mfstock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;

import javax.annotation.PostConstruct;
import java.util.Optional;

@SpringBootApplication
public class MFStockApplication {
    public MFStockConfigLoader props1;
    private static String version;
    private final Logger logger = LoggerFactory.getLogger(MFStockApplication.class);

    public MFStockApplication(MFStockConfigLoader props1) {
        this.props1 = props1;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = new SpringApplicationBuilder(MFStockApplication.class)
                .run(args);
        version = context.getBeansWithAnnotation(SpringBootApplication.class).entrySet().stream()
                .findFirst()
                .flatMap(es -> {
                    final String implementationVersion = es.getValue().getClass().getPackage().getImplementationVersion();
                    return Optional.ofNullable(implementationVersion);
                }).orElse("unknown");
    }

    @PostConstruct
    public void postInit() {
        logger.info(props1.getMfStockConfig().toString());
    }
}
