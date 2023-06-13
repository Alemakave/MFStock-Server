package ru.alemakave.slib.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    public static boolean isPrintErrorLine = true;
    private static boolean isPrintErrored = false;
    public static boolean DEBUG = false;

    public static void info(String msg) {
        print(LogType.INFO, "\r\u001B[K" + msg + "\n>");
    }

    public static void infoF(String msg, Object... msgComponents) {
        if (msg.startsWith("\n"))
            printf(LogType.INFO, msg, msgComponents);
        else
            printf(LogType.INFO, "\r\u001B[K" + msg, msgComponents);
    }

    public static void debug(String msg) {
        print(LogType.DEBUG, "\r\u001B[K" + msg + "\n>");
    }

    public static void debug(Exception exc) {
        debug(exc.getMessage(), exc);
    }

    public static void debug(String msg, Exception exc) {
        debugF("%s: %s\n", exc.getClass().getName(), msg);
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            debug("\t" + stackTraceElement.toString());
        }
    }

    public static void debugF(String msg, Object... msgComponents) {
        printf(LogType.DEBUG, "\r\u001B[K" + msg, msgComponents);
    }

    public static void warn(String msg) {
        print(LogType.WARN, "\r\u001B[K" + msg + "\n>");
    }

    public static void warn(String msg, int stepup) {
        print(LogType.WARN, "\r\u001B[K" + msg + "\n>", stepup);
    }

    public static void warn(Throwable thr) {
        warn(thr.getMessage(), thr);
    }

    public static void warn(Exception exc) {
        warn(exc.getMessage(), exc);
    }

    public static void warn(String msg, Exception exc) {
        warnF("%s: %s\n", exc.getClass().getName(), msg);
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            warn("\t" + stackTraceElement.toString());
        }
    }

    public static void warn(String msg, Throwable thr) {
        warnF("%s: %s\n", thr.getClass().getName(), msg);
        for (StackTraceElement stackTraceElement : thr.getStackTrace()) {
            warn("\t" + stackTraceElement.toString());
        }
    }

    public static void warnF(String msg, Object... msgComponents) {
        printf(LogType.WARN, "\r\u001B[K" + msg, msgComponents);
    }

    public static void error(String msg) {
        print(LogType.ERROR, "\r\u001B[K" + msg + "\n>");
    }

    public static void error(String msg, int stepup) {
        print(LogType.ERROR, "\r\u001B[K" + msg + "\n>", stepup);
    }

    public static void errorF(String msg, Object... msgComponents) {
        printf(LogType.ERROR, "\r\u001B[K" + msg, msgComponents);
    }

    public static void error(String msg, Exception exc) {
        printf(LogType.ERROR, "\r\u001B[K" + "%s: %s\n", exc.getClass().getName(), msg);
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            print(LogType.ERROR, "\r\u001B[K\t" + stackTraceElement.toString() + "\n>");
        }
    }

    public static void error(String msg, Throwable thr) {
        printf(LogType.ERROR, "\r\u001B[K" + "%s: %s\n", thr.getClass().getName(), msg);
        for (StackTraceElement stackTraceElement : thr.getStackTrace()) {
            print(LogType.ERROR, "\r\u001B[K\t" + stackTraceElement.toString() + "\n>");
        }
    }

    public static void error(Throwable exc) {
        printf(LogType.ERROR, "\r\u001B[K" + "%s: %s\n", exc.getClass().getName(), exc.getMessage());
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            print(LogType.ERROR, "\r\u001B[K\t" + stackTraceElement.toString() + "\n>");
        }
    }

    public static void error(Throwable exc, int stepup) {
        print(LogType.ERROR, String.format("\r\u001B[K" + "%s: %s\n", exc.getClass().getName(), exc.getMessage()), stepup);
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            print(LogType.ERROR, "\r\u001B[K\t" + stackTraceElement.toString() + "\n>", stepup);
        }
    }

    public static void fatal(String msg) {
        print(LogType.FATAL_ERROR, "\r\u001B[K" + msg + "\n");
        System.exit(2);
    }

    public static void fatal(Exception exc) {
        print(LogType.FATAL_ERROR, "\r\u001B[K" + exc.getClass() + ": " + exc.getMessage() + "\n");
        for (StackTraceElement stackTraceElement : exc.getStackTrace()) {
            print(LogType.FATAL_ERROR, "\r\u001B[K\t" + stackTraceElement.toString() + "\n");
        }
        System.exit(2);
    }

    public static void fatalF(String msg, Object... msgComponents) {
        printf(LogType.FATAL_ERROR, "\r" + msg + "\n", msgComponents);
        System.exit(2);
    }

    public static void print(LogType logType, String msg) {
        try {
            if (logType == LogType.DEBUG && !DEBUG)
                return;
            StringBuilder prefix = new StringBuilder();
            String postfix = msg.endsWith("\n") ? ">" : "";
            boolean removedPrefix = true;
            while (removedPrefix) {
                if (msg.startsWith("\r")) {
                    msg = msg.substring("\r".length());
                    prefix.append("\r");
                    continue;
                } else if (msg.startsWith("\n")) {
                    msg = msg.substring("\n".length());
                    prefix.append("\n");
                    continue;
                } else if (msg.startsWith("\u001B[K")) {
                    msg = msg.substring("\u001B[K".length());
                    prefix.append("\u001B[K");
                    continue;
                }
                removedPrefix = false;
            }
            msg = msg.replaceAll("\t", "    ");

            String key = "\u001B[1m" + logType.name().toUpperCase();
            switch (logType) {
                case ERROR:
                    key = "\u001B[91m" + key;
                    if (!isPrintErrorLine) break;
                    String[] cpSplited = new Exception("Debug").getStackTrace()[2].toString().split("\\.");
                    StringBuilder cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case FATAL_ERROR:
                    if (!isPrintErrored) {
                        key = "\u001B[91m" + key;
                        key += String.format(":%s:%s", new Exception("Debug").getStackTrace()[3], new Exception("Debug").getStackTrace()[4]);
                    }
                    break;
                case DEBUG:
                    if (!DEBUG) break;
                    key = "\u001B[33m" + key;
                    break;
                case WARN:
                    key = "\u001B[38;5;" + 202 + "m" + key;
                    StackTraceElement[] excStackTrace = new Exception("Debug").getStackTrace();
                    cpSplited = excStackTrace[2].toString().split("\\.");
                    cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case INFO:
                default:
                    break;
            }
            key += "\u001B[0m";
            System.out.print(prefix.toString() + new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss").format(new Date()) + " [" + key + "]: " + msg + postfix);
        } catch (ArrayIndexOutOfBoundsException ignore) {
            isPrintErrored = true;
            print(logType, msg);
        }
        isPrintErrored = false;
    }

    public static void print(LogType logType, String msg, int stepup) {
        try {
            if (logType == LogType.DEBUG && !DEBUG)
                return;
            StringBuilder prefix = new StringBuilder();
            String postfix = msg.endsWith("\n") ? ">" : "";
            boolean removedPrefix = true;
            while (removedPrefix) {
                if (msg.startsWith("\r")) {
                    msg = msg.substring("\r".length());
                    prefix.append("\r");
                    continue;
                } else if (msg.startsWith("\n")) {
                    msg = msg.substring("\n".length());
                    prefix.append("\n");
                    continue;
                } else if (msg.startsWith("\u001B[K")) {
                    msg = msg.substring("\u001B[K".length());
                    prefix.append("\u001B[K");
                    continue;
                }
                removedPrefix = false;
            }
            msg = msg.replaceAll("\t", "    ");

            String key = "\u001B[1m" + logType.name().toUpperCase();
            switch (logType) {
                case ERROR:
                    key = "\u001B[91m" + key;
                    String[] cpSplited = new Exception("Debug").getStackTrace()[2 + stepup].toString().split("\\.");
                    StringBuilder cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case FATAL_ERROR:
                    if (!isPrintErrored) {
                        key = "\u001B[91m" + key;
                        key += String.format(":%s:%s", new Exception("Debug").getStackTrace()[3], new Exception("Debug").getStackTrace()[4]);
                    }
                    break;
                case DEBUG:
                    if (!DEBUG) break;
                    key = "\u001B[33m" + key;
                    break;
                case WARN:
                    key = "\u001B[38;5;" + 202 + "m" + key;
                    cpSplited = new Exception("Debug").getStackTrace()[2 + stepup].toString().split("\\.");
                    cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case INFO:
                default:
                    break;
            }
            key += "\u001B[0m";
            System.out.print(prefix.toString() + new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss").format(new Date()) + " [" + key + "]: " + msg + postfix);
        } catch (ArrayIndexOutOfBoundsException ignore) {
            isPrintErrored = true;
            print(logType, msg);
        }
        isPrintErrored = false;
    }

    public static void printf(LogType logType, String msg, Object... msgComponents) {
        try {
            if (logType == LogType.DEBUG && !DEBUG)
                return;
            StringBuilder prefix = new StringBuilder();
            String postfix = msg.endsWith("\n") ? ">" : "";
            boolean removedPrefix = true;
            while (removedPrefix) {
                if (msg.startsWith("\r")) {
                    msg = msg.substring("\r".length());
                    prefix.append("\r");
                    continue;
                } else if (msg.startsWith("\n")) {
                    msg = msg.substring("\n".length());
                    prefix.append("\n");
                    continue;
                } else if (msg.startsWith("\u001B[K")) {
                    msg = msg.substring("\u001B[K".length());
                    prefix.append("\u001B[K");
                    continue;
                }
                removedPrefix = false;
            }

            String key = "\u001B[1m" + logType.name().toUpperCase();
            switch (logType) {
                case ERROR:
                    key = "\u001B[91m" + key;
                    String[] cpSplited = new Exception("Debug").getStackTrace()[2].toString().split("\\.");
                    StringBuilder cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case FATAL_ERROR:
                    key = "\u001B[91m" + key;
                    key += String.format(":%s:%s", new Exception("Debug").getStackTrace()[3], new Exception("Debug").getStackTrace()[4]);
                    break;
                case DEBUG:
                    if (!DEBUG)
                        break;
                    key = "\u001B[33m" + key;
                    break;
                case WARN:
                    key = "\u001B[38;5;" + 202 + "m" + key;
                    cpSplited = new Exception("Debug").getStackTrace()[2].toString().split("\\.");
                    cp = new StringBuilder();
                    for (int i = 0; i < cpSplited.length - 3; i++) {
                        cp.append(cpSplited[i].toCharArray()[0]);
                        cp.append(".");
                    }
                    for (int i = cpSplited.length - 3; i < cpSplited.length; i++) {
                        cp.append(cpSplited[i]);
                        if (i != cpSplited.length - 1)
                            cp.append(".");
                    }
                    key += ":" + cp;
                    break;
                case INFO:
                default:
                    break;
            }
            key += "\u001B[0m";
            System.out.printf(prefix + new SimpleDateFormat("E dd.MM.yyyy HH:mm:ss").format(new Date()) + " [" + key + "]: " + msg + postfix, msgComponents);
        } catch (ArrayIndexOutOfBoundsException ignore) {
            isPrintErrored = true;
            print(logType, msg);
        }
    }

    public static void ignore(Object ignore) {}

    public enum LogType {
        INFO,
        DEBUG,
        WARN,
        ERROR,
        FATAL_ERROR
    }
}
