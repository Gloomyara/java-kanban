package ru.mikhailantonov.taskmanager.manager;

import org.junit.jupiter.api.*;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.FileManager;
import ru.mikhailantonov.taskmanager.util.StatusType;
import ru.mikhailantonov.taskmanager.util.TimeStampsCrossingException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static ru.mikhailantonov.taskmanager.util.TimeStampsManager.taskTimeValidation;

abstract class TaskManagerTest<T extends TaskManager> {

    T taskManager;
    Task task1;
    Task task2;
    Task task3;
    EpicTask epicTask1;
    EpicTask epicTask2;
    EpicTask epicTask3;
    SubTask subTask11;
    SubTask subTask21;
    SubTask subTask22;
    SubTask subTask31;
    SubTask subTask32;
    SubTask subTask33;

    @BeforeEach
    void createSomeTasks() {
        task1 = new Task("Test NewTask1", "Test NewTask1 description");
        task2 = new Task("Test NewTask2", "Test NewTask2 description");
        task3 = new Task("Test NewTask3", "Test NewTask3 description");

        epicTask1 = new EpicTask("Test NewEpicTask1", "Test NewEpicTask1 description");
        subTask11 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 0),
                "Test NewSubTask11", StatusType.NEW,
                "Test NewSubTask11 description", 30, epicTask1.getTaskId());

        epicTask2 = new EpicTask("Test NewEpicTask2", "Test NewEpicTask2 description");
        subTask21 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 30),
                "Test NewSubTask21", StatusType.NEW,
                "Test NewSubTask21 description", 30, epicTask2.getTaskId());
        subTask22 = new SubTask(LocalDateTime.of(2023, 1, 1, 1, 0),
                "Test NewSubTask22", StatusType.DONE,
                "Test NewSubTask22 description", 30, epicTask2.getTaskId());

        epicTask3 = new EpicTask("Test NewEpicTask3", "Test NewEpicTask3 description");
        subTask31 = new SubTask(LocalDateTime.of(2023, 1, 1, 1, 30),
                "Test NewSubTask31", StatusType.NEW,
                "Test NewSubTask31 description", 30, epicTask3.getTaskId());
        subTask32 = new SubTask(LocalDateTime.of(2023, 1, 1, 2, 0),
                "Test NewSubTask32", StatusType.NEW,
                "Test NewSubTask32 description", 30, epicTask3.getTaskId());
        subTask33 = new SubTask(LocalDateTime.of(2023, 1, 1, 2, 30),
                "Test NewSubTask33", StatusType.NEW,
                "Test NewSubTask3 description", 30, epicTask3.getTaskId());
    }

    @Test
    void getHistory() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.getTaskObjectById(task1.getTaskId());
        taskManager.getTaskObjectById(epicTask1.getTaskId());
        taskManager.getTaskObjectById(subTask11.getTaskId());
        assertEquals(3, taskManager.getHistory().size(), "Неверное количество задач в истории");
    }

    //дополнительные тесты для расчёта статуса эпика в менеджере
    @Test
    void testEpicTaskTypeInManager() {
        EpicTask testEpicTask;
        SubTask testSubTask1;
        SubTask testSubTask2;
        SubTask testSubTask3;

        testEpicTask = new EpicTask("Эпик1", "aa");
        testEpicTask.setTaskId(1);
        testSubTask1 = new SubTask("Э1 ПодЗадача1", StatusType.NEW, "asdfsa", 1);
        testSubTask1.setTaskId(2);
        testSubTask2 = new SubTask("Э1 ПодЗадача2", StatusType.NEW, "fdfasdf", 1);
        testSubTask2.setTaskId(3);
        testSubTask3 = new SubTask("Э1 ПодЗадача3", StatusType.NEW, "asdf", 1);
        testSubTask3.setTaskId(4);

        taskManager.manageTaskObject(testEpicTask);
        //Когда нет подзадач
        assertTrue(testEpicTask.getSubTaskMap().isEmpty());
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW
                , "У эпика задан неверный статус: " + testEpicTask.getTaskStatus() + "; ожидался NEW");
        //Когда все подзадачи со статусом NEW
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW
                , "У эпика задан неверный статус: " + testEpicTask.getTaskStatus() + "; ожидался NEW");
        //Когда есть подзадачи со статусами NEW и DONE
        testSubTask1.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS
                , "У эпика задан неверный статус: " + testEpicTask.getTaskStatus() + "; ожидался IN_PROGRESS");
        //Когда все подзадачи со статусом DONE
        testSubTask1.setTaskStatus(StatusType.DONE);
        testSubTask2.setTaskStatus(StatusType.DONE);
        testSubTask3.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.DONE
                , "У эпика задан неверный статус: " + testEpicTask.getTaskStatus() + "; ожидался DONE");
        //Когда все подзадачи со статусом IN_PROGRESS
        testSubTask1.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask2.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask3.setTaskStatus(StatusType.IN_PROGRESS);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS
                , "У эпика задан неверный статус: " + testEpicTask.getTaskStatus() + "; ожидался IN_PROGRESS");
    }

    @Test
    void manageTaskObjectShouldThrowExceptionWhenTaskIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTaskObject(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex.getMessage());

    }

    @Test
    void manageTaskObjectShouldThrowExceptionWhenEpicTaskIdIsNull() {
        subTask11.setEpicTaskId(null);
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTaskObject(subTask11)
        );
        Assertions.assertEquals("Ошибка при обработке подзадачи! EpicTaskId = null", ex.getMessage());
    }

    @Test
    void addNewTask() {
        task1.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime);
        taskManager.manageTaskObject(task1);
        final int taskId = task1.getTaskId();
        final Task savedTask = taskManager.getTaskObjectById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task1, savedTask, "Задачи не совпадают.");
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllTasks();

        assertFalse(tasks.isEmpty(), "Задачи нe возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task1, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void renewTask() {
        task1 = new Task(LocalDateTime.of(2023, 1, 1, 0, 15),
                "Test NewTask", StatusType.NEW,
                "Test NewTask description", 30);
        task2 = new Task(LocalDateTime.of(2023, 1, 1, 0, 0),
                "Test renewNewTask", StatusType.IN_PROGRESS,
                "Test renewNewTask description", 45);
        final int taskId = 1;
        task1.setTaskId(1);
        task2.setTaskId(1);
        taskManager.manageTask(task1);
        taskManager.manageTask(task2);
        final Task savedTask = taskManager.getTaskObjectById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task2, savedTask, "Задачи не совпадают.");
        assertEquals(savedTask.getStartTime(), task2.getStartTime());
        assertEquals(savedTask.getDuration(), task2.getDuration());
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), task2), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllTasks();

        assertFalse(tasks.isEmpty(), "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task2, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void manageTaskShouldThrowExceptionWhenTaskIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex.getMessage());

    }

    @Test
    void manageTaskShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTask(task1)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void addNewEpicTask() {
        taskManager.manageTaskObject(epicTask2);
        final int epicTaskId = epicTask2.getTaskId();
        final Task savedTask = taskManager.getEpicTask(epicTaskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(epicTask2, savedTask, "Задачи не совпадают.");
        assertEquals(epicTask2.getTaskStatus(), StatusType.NEW, "Эпику не присвоен статус NEW");
        subTask22.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask22);
        assertEquals(epicTask2.getStartTime(), subTask22.getStartTime(),
                "Неверное время старта работы с эпиком");
        assertEquals(epicTask2.getDuration(), subTask22.getDuration(), "Неверная длительность у эпика");
        subTask21.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask21);
        assertEquals(epicTask2.getTaskStatus(), StatusType.IN_PROGRESS, "Эпику не присвоен статус IN_PROGRESS");
        assertEquals(epicTask2.getStartTime(), subTask21.getStartTime(),
                "Неверное время старта работы с эпиком");
        assertEquals(epicTask2.getDuration(), subTask22.getDuration().plus(subTask21.getDuration()),
                "Неверная длительность у эпика");
        final List<Task> tasks = taskManager.getAllEpicTasks();

        assertFalse(tasks.isEmpty(), "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(epicTask2, tasks.get(0), "Задачи не совпадают.");
        assertEquals(2, taskManager.getOneEpicSubTasks(epicTaskId).size(),
                "Неверное количество подзадач.");
    }

    @Test
    void manageEpicTaskShouldThrowExceptionWhenTaskIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageEpicTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке эпика! Невозможно обработать пустой объект задачи"
                , ex.getMessage());

    }

    @Test
    void manageEpicTaskShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageEpicTask(epicTask1)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void addNewSubTask() {
        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        subTask11.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask11);
        final int taskId = subTask11.getTaskId();
        final Task savedTask = taskManager.getTaskObjectById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subTask11, savedTask, "Задачи не совпадают.");
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), subTask11), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllSubTasks();
        final List<Task> oneEpicSubTasks = taskManager.getOneEpicSubTasks(epicTaskId);

        assertFalse(tasks.isEmpty(), "Задачи нe возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, oneEpicSubTasks.size(), "Неверное количество задач.");
        assertEquals(subTask11, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void renewSubTask() {
        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        subTask11 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 15),
                "Test oldSubTask", StatusType.NEW,
                "Test oldSubTask description", 30, epicTaskId);
        subTask21 = new SubTask(LocalDateTime.of(2023, 1, 1, 0, 0),
                "Test NewSubTask", StatusType.IN_PROGRESS,
                "Test NewSubTask description", 45, epicTaskId);
        taskManager.manageTaskObject(subTask11);
        final int taskId = subTask11.getTaskId();
        subTask21.setTaskId(taskId);
        taskManager.manageTaskObject(subTask21);
        final Task savedTask = taskManager.getTaskObjectById(taskId);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask.getStartTime(), subTask21.getStartTime(), "Время старта задачи не совпадает");
        assertEquals(savedTask.getDuration(), subTask21.getDuration(), "Длительность задачи не совпадает");
        assertEquals(subTask21, savedTask, "Задачи не совпадают.");
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), subTask21), "Временные метки не сохранены");
        final List<Task> tasks = taskManager.getAllSubTasks();
        final List<Task> oneEpicSubTasks = taskManager.getOneEpicSubTasks(epicTaskId);
        assertFalse(tasks.isEmpty(), "Задачи нe возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1, oneEpicSubTasks.size(), "Неверное количество задач.");
        assertEquals(subTask21, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void manageSubTaskShouldThrowExceptionWhenTaskIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageSubTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex.getMessage());

    }

    @Test
    void manageSubTaskShouldThrowExceptionWhenTaskIdIsNull() {
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageSubTask(subTask11)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void manageSubTaskShouldThrowExceptionWhenEpicTaskIdIsNull() {
        subTask11.setEpicTaskId(null);
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageSubTask(subTask11)
        );
        Assertions.assertEquals("Ошибка при обработке подзадачи! EpicTaskId = null", ex.getMessage());
    }

    @Test
    void getTaskObjectById() {
        taskManager.manageTaskObject(task1);
        final int taskId = task1.getTaskId();
        assertNotNull(taskManager.getTask(taskId), "Задача не найдена.");
        assertEquals(task1, taskManager.getTask(taskId), "Задачи не совпадают");

        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        assertNotNull(taskManager.getEpicTask(epicTaskId), "Задача не найдена.");
        assertEquals(epicTask1, taskManager.getEpicTask(epicTaskId), "Задачи не совпадают");

        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        final int subTaskTaskId = subTask11.getTaskId();
        assertNotNull(taskManager.getSubTask(subTaskTaskId), "Задача не найдена.");
        assertEquals(subTask11, taskManager.getSubTask(subTaskTaskId), "Задачи не совпадают");
    }

    @Test
    void getTaskObjectByIdShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getTaskObjectById(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void getTaskObjectByIdShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getTaskObjectById(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void getTask() {

        taskManager.manageTaskObject(task1);
        final int taskId = task1.getTaskId();
        assertNotNull(taskManager.getTask(taskId), "Задача не найдена.");
        assertEquals(task1, taskManager.getTask(taskId), "Задачи не совпадают");
    }

    @Test
    void getTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void getTaskShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void getEpicTask() {

        taskManager.manageTaskObject(epicTask1);
        final int taskId = epicTask1.getTaskId();
        assertNotNull(taskManager.getEpicTask(taskId), "Задача не найдена.");
        assertEquals(epicTask1, taskManager.getEpicTask(taskId), "Задачи не совпадают");
    }

    @Test
    void getEpicTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getEpicTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID: " + taskId + " не найден", ex.getMessage());
    }

    @Test
    void getEpicTaskShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getEpicTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void getSubTask() {
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        final int taskId = subTask11.getTaskId();
        assertNotNull(taskManager.getSubTask(taskId), "Задача не найдена.");
        assertEquals(subTask11, taskManager.getSubTask(taskId), "Задачи не совпадают");
    }

    @Test
    void getSubTaskShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void getSubTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getSubTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Подзадача с ID: " + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void getOneEpicSubTasks() {
        taskManager.manageTaskObject(epicTask1);
        final int epicTaskId = epicTask1.getTaskId();
        subTask11.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask11);
        final int taskId = subTask11.getTaskId();

        taskManager.manageTaskObject(epicTask2);
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        taskManager.manageTaskObject(subTask21);
        final SubTask savedSubTask = (SubTask) taskManager.getSubTask(taskId);
        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(1, taskManager.getOneEpicSubTasks(epicTaskId).size(), "Неверное количество задач.");
        assertEquals(savedSubTask, taskManager.getOneEpicSubTasks(epicTaskId).get(0), "Задачи не совпадают");
    }

    @Test
    void getOneEpicSubTasksShouldReturnEmptyList() {
        taskManager.manageTaskObject(epicTask1);
        final int taskId = epicTask1.getTaskId();
        assertTrue(taskManager.getOneEpicSubTasks(taskId).isEmpty(), "Список подзадач эпика с ид :"
                + taskId + " не пустой");
    }

    @Test
    void getOneEpicSubTasksShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getOneEpicSubTasks(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void getOneEpicSubTasksShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getOneEpicSubTasks(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID:" + taskId + " не найден", ex.getMessage());
    }

    @Test
    void getPrioritizedTasks() {
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
        for (Task task : taskManager.getPrioritizedTasks()) {
            System.out.println(task);
        }
        assertEquals(9, taskManager.getPrioritizedTasks().size(), "Неверное количество задач");
    }

    @Test
    void getPrioritizedTasksShouldReturnEmptyList() {
        assertTrue(taskManager.getPrioritizedTasks().isEmpty(), "Список задач не пустой");
    }

    @Test
    void getAllTasks() {
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(task2);
        taskManager.manageTaskObject(task3);
        List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
    }

    @Test
    void getAllTasksShouldReturnEmptyList() {
        assertTrue(taskManager.getAllTasks().isEmpty(), "Список задач не пустой");
    }

    @Test
    void getAllEpicTasks() {
        taskManager.manageTaskObject(epicTask1);
        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        List<Task> tasks = taskManager.getAllEpicTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
    }

    @Test
    void getAllEpicTasksShouldReturnEmptyList() {
        assertTrue(taskManager.getAllEpicTasks().isEmpty(), "Список эпиков не пустой");
    }

    @Test
    void getAllSubTasks() {

        taskManager.manageTaskObject(epicTask1);
        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        subTask31.setEpicTaskId(epicTask3.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask31);
        List<Task> tasks = taskManager.getAllSubTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
    }

    @Test
    void getAllSubTasksShouldReturnEmptyList() {
        taskManager.manageTaskObject(epicTask1);
        assertTrue(taskManager.getAllSubTasks().isEmpty(), "Список подзадач не пустой");
    }

    @Test
    void deleteOneEpicSubTasks() {
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        final int taskId = subTask11.getTaskId();
        taskManager.manageTaskObject(epicTask2);
        final int epicTaskId = epicTask2.getTaskId();
        subTask21.setEpicTaskId(epicTaskId);
        subTask22.setEpicTaskId(epicTaskId);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask22);
        final SubTask savedSubTask = (SubTask) taskManager.getSubTask(taskId);

        assertNotNull(savedSubTask, "Задача не найдена.");
        assertEquals(2, taskManager.getOneEpicSubTasks(epicTaskId).size(),
                "Неверное количество задач.");
        assertTrue(taskManager.deleteOneEpicSubTasks(epicTaskId),
                "Не удалось удалить подзадачи эпика с ид " + epicTaskId);
        assertEquals(1, taskManager.getOneEpicSubTasks(epicTask1.getTaskId()).size(),
                "Неверное количество задач.");
        assertTrue(taskManager.getOneEpicSubTasks(epicTaskId).isEmpty(),
                "Не удалось удалить подзадачи эпика с ид " + epicTaskId);
    }

    @Test
    void deleteOneEpicSubTasksShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteOneEpicSubTasks(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID:" + taskId + " не найден", ex.getMessage());
    }

    @Test
    void deleteOneEpicSubTasksShouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteOneEpicSubTasks(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void deleteOneEpicSubTasksShouldReturnFalseIfTasksMapsIsEmpty() {

        taskManager.manageTaskObject(epicTask1);
        final int taskId = epicTask1.getTaskId();
        assertFalse(taskManager.deleteOneEpicSubTasks(taskId),
                "Ошибка! метод deleteOneEpicSubTasks что то удалил в эпике с ид: " + taskId);

    }


    @Test
    void deleteTaskById() {
        taskManager.manageTaskObject(task1);
        assertEquals(1, taskManager.getAllTasks().size(), "Список задач пуст");
        assertTrue(taskManager.deleteTask(task1.getTaskId()), "задача не удалена");
        assertEquals(0, taskManager.getAllTasks().size(), "задача не удалена");

        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        assertEquals(1, taskManager.getAllSubTasks().size(), "Список подзадач пуст");
        assertTrue(taskManager.deleteSubTask(subTask11.getTaskId()), "задача не удалена");
        assertEquals(0, taskManager.getAllSubTasks().size(), "задача не удалена");

        assertEquals(1, taskManager.getAllEpicTasks().size(), "Список эпиков пуст");
        assertTrue(taskManager.deleteEpicTask(epicTask1.getTaskId()), "задача не удалена");
        assertEquals(0, taskManager.getAllEpicTasks().size(), "задача не удалена");
    }

    @Test
    void deleteTaskByIdShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteTaskObjectById(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void deleteTaskByIdShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteTaskObjectById(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void deleteTask() {
        task1.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime);
        taskManager.manageTaskObject(task1);
        assertEquals(1, taskManager.getAllTasks().size(), "Список задач пуст");
        assertTrue(taskManager.deleteTask(task1.getTaskId()), "задача не удалена");
        assertEquals(0, taskManager.getAllTasks().size(), "задача не удалена");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1)
                , "Временные метки не были удалены");
    }

    @Test
    void deleteTaskShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void deleteTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void deleteEpicTask() {
        taskManager.manageTaskObject(epicTask1);
        assertEquals(1, taskManager.getAllEpicTasks().size(), "Список эпиков пуст");
        assertTrue(taskManager.deleteEpicTask((epicTask1.getTaskId())), "задача не удалена");
        assertEquals(0, taskManager.getAllEpicTasks().size(), "задача не удалена");
    }

    @Test
    void deleteEpicTaskShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteEpicTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void deleteEpicTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteEpicTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID:" + taskId + " не найден", ex.getMessage());
    }

    @Test
    void deleteSubTask() {
        taskManager.manageTaskObject(epicTask1);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        taskManager.manageTaskObject(subTask11);
        assertEquals(1, taskManager.getAllSubTasks().size(), "Список подзадач пуст");
        assertTrue(taskManager.deleteSubTask(subTask11.getTaskId()), "задача не удалена");
        assertEquals(0, taskManager.getAllSubTasks().size(), "задача не удалена");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask11)
                , "Временные метки не были удалены");
    }

    @Test
    void deleteSubTaskShouldThrowExceptionWhenTaskIdIsNull() {

        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteSubTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex.getMessage());
    }

    @Test
    void deleteSubTaskShouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteSubTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Подзадача с ID: " + taskId + " не найдена", ex.getMessage());
    }

    @Test
    void deleteAllTasks() {
        task1.setDuration(Duration.ofMinutes(30));
        task2.setDuration(Duration.ofMinutes(30));
        task3.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime);
        task2.setStartTime(startTime.plusMinutes(30));
        task3.setStartTime(startTime.plusMinutes(60));
        taskManager.manageTaskObject(task1);
        taskManager.manageTaskObject(task2);
        taskManager.manageTaskObject(task3);
        List<Task> tasks = taskManager.getAllTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        assertTrue(taskManager.deleteAllTasks(), "Удаление всех задач не удалось");
        tasks = taskManager.getAllTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех задач не удалось");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task2)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task3)
                , "Временные метки не были удалены");
    }

    @Test
    void deleteAllTasksShouldReturnFalseIfTasksMapsIsEmpty() {
        assertFalse(taskManager.deleteAllTasks(),
                "Ошибка! метод deleteAllTasks что то удалил");
    }

    @Test
    void deleteAllEpicTasks() {
        taskManager.manageTaskObject(epicTask1);

        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        List<Task> tasks = taskManager.getAllEpicTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        assertTrue(taskManager.deleteAllEpicTasks(), "Удаление всех эпиков не удалось");
        tasks = taskManager.getAllEpicTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех эпиков не удалось");
    }

    @Test
    void deleteAllEpicTasksShouldReturnFalseIfTasksMapsIsEmpty() {
        assertFalse(taskManager.deleteAllEpicTasks(),
                "Ошибка! метод deleteAllEpicTasks что то удалил");
    }

    @Test
    void deleteAllSubTasks() {

        taskManager.manageTaskObject(epicTask1);
        taskManager.manageTaskObject(epicTask2);
        taskManager.manageTaskObject(epicTask3);
        subTask11.setEpicTaskId(epicTask1.getTaskId());
        subTask21.setEpicTaskId(epicTask2.getTaskId());
        subTask31.setEpicTaskId(epicTask3.getTaskId());
        taskManager.manageTaskObject(subTask11);
        taskManager.manageTaskObject(subTask21);
        taskManager.manageTaskObject(subTask31);
        List<Task> tasks = taskManager.getAllSubTasks();
        assertFalse(tasks.isEmpty(), "Список задачи пуст");
        assertEquals(3, tasks.size(), "Количество задач не совпадает");
        assertTrue(taskManager.deleteAllSubTasks(), "Удаление всех подзадач не удалось");
        tasks = taskManager.getAllSubTasks();
        assertTrue(tasks.isEmpty(), "Удаление всех подзадач не удалось");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask11)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask21)
                , "Временные метки не были удалены");
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), subTask31)
                , "Временные метки не были удалены");
    }

    @Test
    void deleteAllSubTasksShouldReturnFalseIfTasksMapsIsEmpty() {
        taskManager.manageTaskObject(epicTask1);
        assertFalse(taskManager.deleteAllSubTasks(),
                "Ошибка! метод deleteAllSubTasks что то удалил");
    }

    @Test
    void manageTaskObjectByIdShouldNotSaveTimeStamps() {
        task1.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime);
        task2.setDuration(Duration.ofMinutes(30));
        taskManager.manageTaskObject(task2);
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные метки сохранены");
        task3.setStartTime(startTime);
        taskManager.manageTaskObject(task3);
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные метки сохранены");
        task3.setDuration(Duration.ofMinutes(30));
        task3.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(task3);
        assertTrue(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные метки сохранены");
        taskManager.manageTaskObject(task1);
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные не метки сохранены");
    }
    @Test
    void shouldThrowExceptionWhenTimeStampsIsCrossing() {
        task1.setDuration(Duration.ofMinutes(45));
        LocalDateTime startTime1 = LocalDateTime.of(2023, 1, 1, 0, 0);
        task1.setStartTime(startTime1);
        task2.setDuration(Duration.ofMinutes(30));
        LocalDateTime startTime2 = startTime1.plusMinutes(30);
        task2.setStartTime(startTime2);
        taskManager.manageTaskObject(task1);
        assertFalse(taskTimeValidation(taskManager.getTimeStampsSet(), task1), "Временные метки не сохранены");
        TimeStampsCrossingException ex = Assertions.assertThrows(
                TimeStampsCrossingException.class,
                () -> taskManager.manageTaskObject(task2)
        );
        Assertions.assertEquals("Ошибка! На время С: "
                + startTime2.format(FileManager.DATE_TIME_FORMATTER)
                + " до: " + task2.getEndTime().format(FileManager.DATE_TIME_FORMATTER)
                + " уже запланировано выполнение других задач", ex.getMessage());

        task2.setStartTime(startTime2.plusMinutes(15));
        taskManager.manageTaskObject(task2);
        task3.setStartTime(startTime2);
        task3.setDuration(Duration.ofMinutes(30));
        task3.setTaskStatus(StatusType.IN_PROGRESS);
        task3.setTaskId(task2.getTaskId());

        TimeStampsCrossingException ex1 = Assertions.assertThrows(
                TimeStampsCrossingException.class,
                () -> taskManager.manageTaskObject(task3)
        );
        Assertions.assertEquals("Не удалось обновить " + task3.getTaskType()
                + " Id: " + task3.getTaskId() + "; " + "Ошибка! На время С: "
                + startTime2.format(FileManager.DATE_TIME_FORMATTER)
                + " до: " + task3.getEndTime().format(FileManager.DATE_TIME_FORMATTER)
                + " уже запланировано выполнение других задач", ex1.getMessage());
    }
}