package ru.mikhailantonov.taskmanager.util.exceptions;

/**
 * собственное исключение для обработки ошибок во время сохранения данных
 * через метод save() в FileBackedTasksManager
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
