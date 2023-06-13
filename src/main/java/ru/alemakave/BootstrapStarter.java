package ru.alemakave;

import ru.alemakave.slib.utils.Lib;
import ru.alemakave.slib.utils.LibUtils;
import ru.alemakave.slib.utils.Logger;
import ru.alemakave.slib.utils.OS;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.ArrayList;
import java.util.List;

import static ru.alemakave.slib.utils.OS.OperationSystem.WINDOWS;
import static ru.alemakave.slib.utils.OS.sep;

public class BootstrapStarter {
    public static String libsDir = null;
    private static LibUtils lu;
    private static List<String> jvmArgs = new ArrayList<>();
    private static final List<Lib> libs = new ArrayList<>();
    private static final List<String> arguments = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length > 0) {
            for (String s : args) {
                if (s.startsWith("--help")) {
                    System.out.println("\rArguments List:");
                    System.out.println("--installLib=[GroupID:ArtifactID:Version]    install lib");
                    System.out.println("--libsDir=[path]                             set libs directory");
                    System.out.println("--loadLibsAndExit                            load libs");

                    System.out.println("--port=[port]                        - setup server port");
                    System.out.println("--dbPath=[path to data base]         - setup path to data base");
                    System.out.println("--dbConfigsPath=[path to db configs] - setup path to data base config file");
                    System.out.println("--generateDBConfigs                  - generate example data base config file");
                    System.out.println("--help                               - show this info");
                    return;
                }
                else if (s.startsWith("--libsDir=")) {
                    libsDir = s.replaceAll("--libsDir=", "");
                } else if (s.startsWith("--loadLibsAndExit")) {
                    if (!initLibs(libsDir))
                        Logger.fatal("Libs not installed!");
                    return;
                } else if (s.toLowerCase().startsWith("--installLib=".toLowerCase())) {
                    libs.add(new Lib(s.substring("--installLib=".length())));
                }

                arguments.add(s);
            }
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            jvmArgs = runtimeMXBean.getInputArguments();
        }

        if (!initLibs(libsDir))
            Logger.fatal("Libs not installed!");

        List<String> command = new ArrayList<>();
        command.add(System.getProperty("java.home") + sep + "bin" + sep + "java" + (OS.OperationSystem.current() == WINDOWS ? ".exe" : ""));
        command.add("-Dfile.encoding=UTF-8");
        command.addAll(jvmArgs);
        command.add("-cp");
        command.add(lu.getClassPathCommandPart());
        command.add("ru.alemakave.Bootstrap");
        command.addAll(arguments);
        ProcessBuilder bootstrapProcessBuilder = runProcess(command);
        Process bootstrapProcess = bootstrapProcessBuilder.start();

        while (bootstrapProcess.isAlive()) {
            //noinspection BusyWait
            Thread.sleep(10);
        }
        Logger.info("Process closed. Exit code: " + bootstrapProcess.exitValue());
        if (bootstrapProcess.exitValue() == 255) {
            lu.clear();
            main(args);
        }
    }

    private static ProcessBuilder runProcess(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectInput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        processBuilder.redirectErrorStream(true);
        return processBuilder;
    }

    @SuppressWarnings("unused")
    private static ProcessBuilder runProcess(List<String> command, ProcessBuilder.Redirect redirectInput, ProcessBuilder.Redirect redirectOutput, boolean redirectError) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectInput(redirectInput);
        processBuilder.redirectOutput(redirectOutput);
        processBuilder.redirectErrorStream(redirectError);
        return processBuilder;
    }

    private static boolean initLibs(String libsDir) {
        if (libsDir == null || libsDir.equals(""))
            libsDir = OS.getJarDir(BootstrapStarter.class) + sep + "Libs";

        String jettyVersion = "9.2.19.v20160908";
        //noinspection UnnecessaryLocalVariable
        String jettyJspVersion = jettyVersion;

        libs.add(new Lib("javax.servlet", "javax.servlet-api", "3.1.0"));
        libs.add(new Lib("org.eclipse.jetty", "jetty-http", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-io", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-jsp", jettyJspVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-jmx", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-security", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-server", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-servlet", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-proxy", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-util", jettyVersion));
        libs.add(new Lib("org.eclipse.jetty", "jetty-webapp", jettyVersion));
        libs.add(new Lib("org.jetbrains", "annotations", "15.0"));

        libs.add(new Lib("org.slf4j", "slf4j-api", "1.7.25"));
        libs.add(new Lib("org.slf4j", "slf4j-simple", "1.7.25"));

        libs.add(new Lib("com.fasterxml.jackson.core", "jackson-core", "2.10.5"));
        libs.add(new Lib("com.fasterxml.jackson.core", "jackson-annotations", "2.10.5"));
        libs.add(new Lib("com.fasterxml.jackson.core", "jackson-databind", "2.10.5"));

        lu = new LibUtils(libsDir, libs);
        lu.addRepository("Sonatype Nexus Snapshots", "https://oss.sonatype.org/content/repositories/snapshots/");
        lu.addRepository("Sonatype Nexus Staging", "https://oss.sonatype.org/service/local/staging/deploy/maven2/");
        lu.addRepository("Sonatype Nexus 1", "https://oss.sonatype.org/service/local/repositories/releases/content/");
        lu.addRepository("MVN", " http://mvnrepository.com/artifact/");

        return lu.installLibsWithoutDependency();
    }
}
