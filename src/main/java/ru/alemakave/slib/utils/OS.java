package ru.alemakave.slib.utils;

import java.net.URISyntaxException;

public final class OS {
    public static String sep;
    private static String jarPath = getJarPath(OS.class);

    public static String getJarPath(Class clazz) {
        try {
            jarPath = clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        if (OperationSystem.current() == OperationSystem.WINDOWS && jarPath.startsWith("/")) {
            jarPath = jarPath.substring(1);
            if (jarPath.contains("/")) {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < jarPath.length(); i++) {
                    char c = jarPath.charAt(i);
                    if (c == '/') {
                        sb.append('\\');
                        continue;
                    }
                    sb.append(c);
                }
                jarPath = sb.toString();
            }
        }

        return jarPath;
    }

    public static String getJarName(Class clazz) {
        String[] jarPathParts;
        if (OperationSystem.current() == OperationSystem.WINDOWS)
            jarPathParts = getJarPath(clazz).split("\\\\");
        else
            jarPathParts = getJarPath(clazz).split(sep);
        return jarPathParts[jarPathParts.length-1];
    }

    public static String getJarDir(Class clazz) {
        if (OperationSystem.current() == OperationSystem.WINDOWS)
            return getJarPath(clazz).replaceAll("\\\\" + getJarName(clazz), "");
        else
            return getJarPath(clazz).replaceAll(sep + getJarName(clazz), "");
    }

    public static String getOS() {
        return System.getProperty("os.name");
    }

    static {
        sep = System.getProperty("file.separator");
        if (OperationSystem.current() == OperationSystem.WINDOWS)
            sep = "\\\\";
    }

    public enum OperationSystem {
        UNKNOWN,
        WINDOWS,
        MAC_OS,
        LINUX;

        public static OperationSystem current() {
            if (getOS().equalsIgnoreCase("Linux")) {
                return LINUX;
            }
            if (getOS().startsWith("Windows"))
                return WINDOWS;
            else {
                try {
                    throw new Exception(String.format("Unknown OS! (%s)", getOS()));
                } catch (Exception e) {
                    Logger.fatal(e);
                }
                return UNKNOWN;
            }
        }

        @Override
        public String toString() {
            return getOS();
        }
    }

    public enum OSBitType {
        X32,
        X64
    }
}
