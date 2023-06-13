package ru.alemakave.slib.utils;

public class ArgumentsUtils {
    public static String getValue(String argument) {
        String[] argumentParts = argument.split("=", 2);
        return getValue(argument, argumentParts[0] + "=");
    }

    public static String getValue(String argument, String key) {
        return argument.substring(key.length());
    }
}
