package ru.mikhailantonov.taskmanager.server.handlers;

import com.google.gson.*;
import ru.mikhailantonov.taskmanager.task.Task;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class TaskDeserializer implements JsonDeserializer<Task> {
    private String taskTypeElementName;
    private Gson gson;
    private Map<String, Class<? extends Task>> taskTypeRegistry;

    public TaskDeserializer(String taskTypeElementName) {
        this.taskTypeElementName = taskTypeElementName;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeTypeAdapter())
                .create();
        this.taskTypeRegistry = new HashMap<>();
    }

    public void registerTaskType(String taskTypeName, Class<? extends Task> taskType) {
        taskTypeRegistry.put(taskTypeName, taskType);
    }

    public Task deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        JsonObject taskObject = json.getAsJsonObject();
        JsonElement taskTypeElement = taskObject.get(taskTypeElementName);

        Class<? extends Task> taskType = taskTypeRegistry.get(taskTypeElement.getAsString());
        return gson.fromJson(taskObject, taskType);
    }
}