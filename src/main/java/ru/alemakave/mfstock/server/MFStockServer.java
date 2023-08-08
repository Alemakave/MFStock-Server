package ru.alemakave.mfstock.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.ServletContextHandler;
import ru.alemakave.mfstock.json.DBConfigs;
import ru.alemakave.mfstock.server.servlet.MainServlet;
import ru.alemakave.mfstock.server.servlet.commands.*;
import ru.alemakave.slib.servlet.ServletCommandManager;
import ru.alemakave.slib.utils.ArgumentsUtils;
import ru.alemakave.slib.utils.Logger;
import ru.alemakave.slib.utils.PrintUtils;
import ru.alemakave.xlsx_parser.XLSX;

import javax.print.PrintService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class MFStockServer {
    public static final Configs configs = new Configs();
    public static XLSX xlsxDatabase;
    public static DBConfigs dbConfigs;
    public static File propertiesFile = new File("MFStockServer.properties");
    public static Properties props = new Properties();

    public static void run(String... args) {
        try {
            File dbConfigsFile = null;
            for (String arg : args) {
                if (arg.startsWith("--help")) {
                    System.out.println("--port=[port]                        - setup server port");
                    System.out.println("--dbPath=[path to data base]         - setup path to data base");
                    System.out.println("--dbConfigsPath=[path to db configs] - setup path to data base config file");
                    System.out.println("--generateDBConfigs                  - generate example data base config file");
                    System.out.println("--help                               - show this info");
                }
                else if (arg.startsWith("--port=")) {
                    configs.port = ArgumentsUtils.getValue(arg);
                    if (Integer.parseInt(configs.port) > 65535 || Integer.parseInt(configs.port) < 0) {
                        Logger.error(String.format("Port %s out of range. Set port 8080", configs.port));
                        configs.port = "8080";
                    }
                } else if (arg.startsWith("--dbPath=")) {
                    configs.dbFilePath = ArgumentsUtils.getValue(arg);
                } else if (arg.startsWith("--dbConfigsPath=")) {
                    dbConfigsFile = new File(ArgumentsUtils.getValue(arg));
                } else if (arg.startsWith("--generateDBConfigs")) {
                    ObjectMapper mapper = new ObjectMapper();
                    if (dbConfigsFile == null) {
                        dbConfigsFile = new File("DBConfigs.json");
                    }
                    if (dbConfigsFile.exists()) {
                        //noinspection ResultOfMethodCallIgnored
                        dbConfigsFile.delete();
                    }
                    dbConfigs = new DBConfigs();
                    dbConfigs.columns = new DBConfigs.DBColumnConfigs[] { new DBConfigs.DBColumnConfigs() };
                    mapper.writeValue(dbConfigsFile, dbConfigs);
                    System.exit(0);
                }
            }

            if (dbConfigsFile == null) {
                dbConfigsFile = new File("DBConfigs.json");
            }

            if (!propertiesFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                propertiesFile.createNewFile();
                props.put("printerName", "");
                try (FileOutputStream propsOutputStream = new FileOutputStream(propertiesFile)) {
                    PrintService[] printers = PrintUtils.getPrinters();
                    StringBuilder printerNames = new StringBuilder("Select and enter printer name to \"printerName\" to use for stickers printing\n" +
                            "Printers: \n");
                    for (PrintService printService : printers) {
                        printerNames.append(printService.getName()).append("\n");
                    }
                    props.store(propsOutputStream, printerNames.toString());
                }
            }

            try (FileInputStream propsInputStream = new FileInputStream(propertiesFile)) {
                props.load(propsInputStream);
            }

            System.out.println("Selected printer: " + props.get("printerName"));

            ObjectMapper mapper = new ObjectMapper();
            if (dbConfigsFile.exists()) {
                dbConfigs = mapper.readValue(dbConfigsFile, DBConfigs.class);
            }

            loadDB();

            //noinspection InstantiationOfUtilityClass
            new MFStockServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadDB() throws Exception {
        xlsxDatabase = new XLSX(configs.dbFilePath);
        ArrayList<String> columnsFilter = new ArrayList<>();
        for (DBConfigs.DBColumnConfigs dbColumnConfig : dbConfigs.columns) {
            String columnHeaderText = dbColumnConfig.headerText;
            String prefix = dbColumnConfig.prefix;
            if (prefix != null && !prefix.isEmpty())
                xlsxDatabase.getWorkbook().sheets.get(0).sheetData.addColumnDataPrefix(columnHeaderText, prefix);
            columnsFilter.add(columnHeaderText);
        }
        xlsxDatabase.getWorkbook().sheets.get(0).sheetData.filterColumns(columnsFilter);
    }

    public MFStockServer() throws Exception {
        Server server = new Server(Integer.parseInt(configs.port));

        ServletContextHandler servletHandler = new ServletContextHandler();
        servletHandler.setContextPath("/");
        servletHandler.addServlet(MainServlet.class, "/");

        HandlerList handlers = new HandlerList();
        handlers.addHandler(servletHandler);

        ServletCommandManager.getManager().registryServletCommand(new MFStockFindFromScan());
        ServletCommandManager.getManager().registryServletCommand(new MFStockGetDBDate());
        ServletCommandManager.getManager().registryServletCommand(new MFStockGenerateNomSticker());
        ServletCommandManager.getManager().registryServletCommand(new MFStockGenerateSerSticker());
        ServletCommandManager.getManager().registryServletCommand(new MFStockCloseDB() {
            @Override
            public void call(HttpMethod method, HttpServletRequest req, HttpServletResponse resp) throws IOException {
                xlsxDatabase = null;
                resp.getOutputStream().write(getPageData().getBytes());
                System.gc();
            }
        });

        server.setHandler(handlers);
        server.start();
        System.out.println("*** This opensource project ***");
        System.out.println("Sources: https://github.com/Alemakave/MFStock-Server");
    }
}
