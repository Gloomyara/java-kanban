package ru.mikhailantonov.taskmanager.server.enums;

public enum HttpEndPoint {
    TASK_ENDPOINT("task"),
    EPIC_ENDPOINT("epic"),
    SUBTASK_ENDPOINT("subtask"),
    HISTORY_ENDPOINT("history"),
    UNKNOWN_ENDPOINT("unknown");

    private final String endPointName;

    HttpEndPoint(String taskTypeName) {
        this.endPointName = taskTypeName;
    }

    public String getEndPointName() {
        return endPointName;
    }

    public static HttpEndPoint fromString(String str) {
        for (HttpEndPoint type : HttpEndPoint.values()) {
            if (type.getEndPointName().equalsIgnoreCase(str)) return type;
        }
        return UNKNOWN_ENDPOINT;
    }

    @Override
    public String toString() {
        return "Тип задачи: " + endPointName;
    }
}

