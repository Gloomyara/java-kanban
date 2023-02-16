package ru.mikhailantonov.taskmanager.util.exceptions;


/**
 * собственное исключение для обработки ошибок, выбрасывается при обнаружении пересечений временных меток
 */
public class TimeStampsCrossingException extends RuntimeException {
    public TimeStampsCrossingException(final String message) {
        super(message);
    }
}

