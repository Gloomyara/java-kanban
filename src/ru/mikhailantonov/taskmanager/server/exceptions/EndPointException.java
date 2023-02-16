package ru.mikhailantonov.taskmanager.server.exceptions;

public class EndPointException extends RuntimeException {
    public EndPointException(String message) {
        super(message);
    }
}
