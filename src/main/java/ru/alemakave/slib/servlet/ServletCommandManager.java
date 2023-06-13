package ru.alemakave.slib.servlet;

import java.util.HashMap;

public class ServletCommandManager {
    private static final ServletCommandManager INSTANCE = new ServletCommandManager();
    private final HashMap<String, IServletCommand> commandsCache = new HashMap<>();

    public IServletCommand getServletCommand(String command) {
        return commandsCache.get(command);
    }

    public void registryServletCommand(IServletCommand command) {
        commandsCache.put(command.getCommand(), command);
    }

    public static ServletCommandManager getManager() {
        return INSTANCE;
    }
}
