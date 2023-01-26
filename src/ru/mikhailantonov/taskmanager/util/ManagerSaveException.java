package ru.mikhailantonov.taskmanager.util;

/**
 * собственное исключение для обработки ошибок во время сохранения данных
 * через метод save() в FileBackedTasksManager
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }
}
