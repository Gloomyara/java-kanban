package ru.mikhailantonov.taskmanager.util;


/**
 * собственное исключение для обработки ошибок, выбрасывается при обнаружении пересечений временных меток
 */
public class TimeStampsCrossingException extends Exception {
    public TimeStampsCrossingException(final String message) {
        super(message);
    }
}

