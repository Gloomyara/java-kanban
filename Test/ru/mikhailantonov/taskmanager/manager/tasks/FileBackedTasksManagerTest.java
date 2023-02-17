package ru.mikhailantonov.taskmanager.manager.tasks;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;
import ru.mikhailantonov.taskmanager.util.Managers;
import ru.mikhailantonov.taskmanager.util.exceptions.ManagerSaveException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void createBackedTasksManager() {
        taskManager = (FileBackedTasksManager) Managers.getDefault(true, true, "testautosave.csv");
    }

    @Test
    void loadFromFileShouldThrowExceptionWhenHistoryIsEmpty() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);

        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(taskManager.getAutoSave())
        );
        Assertions.assertEquals("Не удалось загрузить список истории", ex.getMessage());
    }

    @Test
    void loadFromFileShouldThrowExceptionWhenFileIsEmpty() {

        ManagerSaveException ex = Assertions.assertThrows(
                ManagerSaveException.class,
                () -> FileBackedTasksManager.loadFromFile(taskManager.getAutoSave())
        );
        Assertions.assertEquals("Не удалось загрузить список истории", ex.getMessage());
    }

    @Test
    void loadFromFileWithEmptyEpic() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        taskManager.getTaskObjectById(task1.getTaskId());
        taskManager.getTaskObjectById(epicTask1.getTaskId());

        assertEquals(2, taskManager.getHistory().size(), "неверное количество задач в истории");

        FileBackedTasksManager taskManagerLoaded = FileBackedTasksManager.loadFromFile(taskManager.getAutoSave());

        assertEquals(2, taskManagerLoaded.getHistory().size(), "неверное количество задач в истории");
        assertEquals(task1, taskManagerLoaded.getTaskObjectById(task1.getTaskId())
                , "загруженная задача не соответствует сохраненной");
        assertEquals(epicTask1, taskManagerLoaded.getTaskObjectById(epicTask1.getTaskId())
                , "загруженный эпик не соответствует сохраненному");
    }

    @Test
    void loadFromFile() {
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

        FileBackedTasksManager taskManagerLoaded = FileBackedTasksManager.loadFromFile(taskManager.getAutoSave());
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