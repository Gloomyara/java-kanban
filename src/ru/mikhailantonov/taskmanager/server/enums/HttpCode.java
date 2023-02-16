package ru.mikhailantonov.taskmanager.server.enums;

public enum HttpCode {
    SUCCESS(200),
    CREATED(201),
    ACCEPTED(202),
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    METHOD_NOT_ALLOWED(405),
    INTERNAL_SERVER_ERROR(500);

    private final int code;

    HttpCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
