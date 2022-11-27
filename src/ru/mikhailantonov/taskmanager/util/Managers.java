package ru.mikhailantonov.taskmanager.util;

import ru.mikhailantonov.taskmanager.manager.*;

/** Утилитарный класс для создания менеджеров
 *  когда-нибудь он сможет создавать больше 1 типа менеджера */

public class Managers {
    private static TaskManager t;

    public static TaskManager getDefault() {
        t = new InMemoryTaskManager();
        return t;
    }

    public static String getDefaultHistory() {
        return t.getHistory();
    }
}
