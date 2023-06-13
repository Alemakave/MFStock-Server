package ru.alemakave.mfstock.server.utils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PageUtils {
    public static List<String> getCommands(HttpServletRequest req) {
        if (req.getQueryString() == null || req.getQueryString().equals("null"))
            return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(req.getQueryString().split("&")));
    }

    public static boolean hasCommand(HttpServletRequest req, String command) {
        for (String cmd : getCommands(req)) {
            if (cmd.toLowerCase().startsWith(command.toLowerCase() + "="))
                return true;
            else if (cmd.equalsIgnoreCase(command))
                return true;
        }

        return false;
    }

    public static String getCommandValueFromKey(HttpServletRequest req, String commandKey) {
        String result = null;
        if (hasCommand(req, commandKey)) {
            List<String> pageCommands = getCommands(req);
            for (String command : pageCommands) {
                if (command.toLowerCase().startsWith(commandKey.toLowerCase())) {
                    result = command.substring(commandKey.length() + 1);
                    break;
                }
            }
        }
        return result;
    }
}
