package ru.mikhailantonov.taskmanager.manager.tasks;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.server.HttpTaskServer;
import ru.mikhailantonov.taskmanager.server.KVServer;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.util.Managers;
import ru.mikhailantonov.taskmanager.util.exceptions.ManagerSaveException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kv;

    @BeforeEach
    void createManager() {

        try {
            kv = new KVServer();
            kv.start();
            taskManager = Managers.getDefault("http://localhost:8080");
        } catch (IOException e) {
            System.out.println(e.getMessage() + " || " + e.getCause());
        }
    }

    @AfterEach
    void stopServers() {
        kv.stop();
    }
    @Test
    void GetAllTypeTasksShouldBeIsEmpty() {
        HttpTaskManager taskManager1 = Managers.getDefault("http://localhost:8080",
                taskManager.getAPI_TOKEN());
        taskManager1.loadFromServer();
        assertTrue(taskManager1.getAllTypesTasks().isEmpty(), "Список всех типов задач не пустой");
    }
    @Test
    void HistoryShouldBeIsEmpty() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);

        HttpTaskManager taskManager2 = Managers.getDefault("http://localhost:8080",
                taskManager.getAPI_TOKEN());
        taskManager2.loadFromServer();
        assertTrue(taskManager2.getHistory().isEmpty(), "Список с историей не пустой");
    }

    @Test
    void loadFromServer() {
        task1.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(task2);
        taskManager.manageTaskObject(task3);
        taskManager.manageTaskObject(epicTask2);
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        subTask22.setEpicTaskId(epicTask2.getTaskId());
        subTask21.setTaskStatus(StatusType.DONE);
        subTask22.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask22);
        taskManager.manageTaskObject(epicTask3);
        subTask31.setEpicTaskId(epicTask3.getTaskId());
        subTask32.setEpicTaskId(epicTask3.getTaskId());
        subTask33.setEpicTaskId(epicTask3.getTaskId());
        taskManager.manageTaskObject(subTask31);
        taskManager.manageTaskObject(subTask32);
        taskManager.manageTaskObject(subTask33);
        taskManager.getTaskObjectById(epicTask3.getTaskId());
        taskManager.getTaskObjectById(subTask32.getTaskId());
        taskManager.getTaskObjectById(subTask31.getTaskId());
        taskManager.getTaskObjectById(subTask33.getTaskId());
        taskManager.getTaskObjectById(task1.getTaskId());
        assertEquals(5, taskManager.getHistory().size(), "неверное количество задач в истории");

        HttpTaskManager taskManagerLoaded = Managers.getDefault("http://localhost:8080",
                taskManager.getAPI_TOKEN());
        taskManagerLoaded.loadFromServer();
        assertEquals(5, taskManagerLoaded.getHistory().size(), "неверное количество задач в истории");
        assertEquals(task1, taskManagerLoaded.getTaskObjectById(task1.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(epicTask1, taskManagerLoaded.getTaskObjectById(epicTask1.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask11, taskManagerLoaded.getTaskObjectById(subTask11.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(task2, taskManagerLoaded.getTaskObjectById(task2.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(task3, taskManagerLoaded.getTaskObjectById(task3.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(epicTask2, taskManagerLoaded.getTaskObjectById(epicTask2.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask21, taskManagerLoaded.getTaskObjectById(subTask21.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask22, taskManagerLoaded.getTaskObjectById(subTask22.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(epicTask3, taskManagerLoaded.getTaskObjectById(epicTask3.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask31, taskManagerLoaded.getTaskObjectById(subTask31.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask32, taskManagerLoaded.getTaskObjectById(subTask32.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(subTask33, taskManagerLoaded.getTaskObjectById(subTask33.getTaskId())
                , "загруженная задача не соответствует сохраненной");
    }
}