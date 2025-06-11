package ru.alemakave.mfstock;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.alemakave.mfstock.configs.MFStockConfigLoader;
import ru.alemakave.mfstock.utils.LibUtils;
import ru.alemakave.slib.vc.utils.UpdateUtils;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import javax.annotation.PostConstruct;
import java.nio.file.Path;

@Slf4j
@SpringBootApplication
public class MFStockApplication {
    public MFStockConfigLoader props1;
    private final Logger logger = LoggerFactory.getLogger(MFStockApplication.class);

    public static void main(String[] args) {
        preInit();
        new SpringApplicationBuilder(MFStockApplication.class).run(args);
        log.info("MFStock version: " + BuildInfo.BUILD_VERSION);

        UpdateUtils.checkUpdateFromGradle(BuildInfo.BUILD_VERSION,
                "https://raw.githubusercontent.com/Alemakave/MFStock-Server/master/build.gradle",
                "Update available via link: https://github.com/Alemakave/MFStock-Server/releases");
    }

    public MFStockApplication(MFStockConfigLoader props1) {
        this.props1 = props1;
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
    }

    @SneakyThrows
    private static void preInit() {
        LibUtils.extractLib(Path.of("BOOT-INF/classes/libs/com/jacob/1.20/jacob-1.20-x64.dll"), Path.of("./jacob-1.20-x64.dll"));
    }

    @PostConstruct
    public void postInit() {
        logger.info(props1.getMfStockConfig().toString());
    }
}
