package ru.alemakave.telegram_bot_utils.init;

public class ActionManager {
    private static final ActionManager instance = new ActionManager();

    private ActionManager() {

    }

    public static ActionManager getActionManager() {
        return instance;
    }
}
