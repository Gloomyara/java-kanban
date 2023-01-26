package ru.mikhailantonov.taskmanager.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.task.EpicTask;
import ru.mikhailantonov.taskmanager.task.SubTask;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.StatusType;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

abstract class TaskManagerTest<T extends TaskManager> {
    //    Чтобы избежать дублирования кода, необходим базовый класс
    //    с тестами на каждый метод из интерфейса abstract class TaskManagerTest<T extends TaskManager>.
    //    Для подзадач нужно дополнительно проверить наличие эпика, а для эпика — расчёт статуса.
    //    Для каждого метода нужно проверить его работу:
    //      a. Со стандартным поведением.
    //      b. С пустым списком задач.
    //      c. С неверным идентификатором задачи (пустой и/или несуществующий идентификатор).
    HistoryManager historyManager = new InMemoryHistoryManager();
    T taskManager;
    Task task1;
    Task task2;
    Task task3;
    EpicTask epicTask1;
    EpicTask epicTask2;
    EpicTask epicTask3;
    SubTask subTask1;
    SubTask subTask2;
    SubTask subTask3;
    SubTask subTask4;

    @Test
    void getHistory() {
    }

    /*
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description", NEW);
        final int taskId = taskManager.addNewTask(task);

        final Task savedTask = taskManager.getTask(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }
     */
    @Test
    void manageTaskObject() {

    }

    @Test
    void manageTask() {
    }

    //Когда некорректный TaskId
    @Test
    void shouldThrowExceptionWhenTaskIdIncorrect() {
        int taskId = 123456789;
        NoSuchElementException ex1 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex1.getMessage());

        NoSuchElementException ex2 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getEpicTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID: " + taskId + " не найден", ex2.getMessage());

        NoSuchElementException ex3 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getSubTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Подзадача с ID: " + taskId + " не найдена", ex3.getMessage());

        NoSuchElementException ex4 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getTaskObjectById(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex4.getMessage());

        NoSuchElementException ex5 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteTaskObjectById(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex5.getMessage());

        NoSuchElementException ex6 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Задача с ID:" + taskId + " не найдена", ex6.getMessage());

        NoSuchElementException ex7 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteEpicTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID: " + taskId + " не найден", ex7.getMessage());

        NoSuchElementException ex8 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteSubTask(taskId)
        );
        Assertions.assertEquals("Ошибка! Подзадача с ID: " + taskId + " не найдена", ex8.getMessage());

        NoSuchElementException ex9 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.getOneEpicSubTasks(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID:" + taskId + " не найден", ex9.getMessage());

        NoSuchElementException ex10 = Assertions.assertThrows(
                NoSuchElementException.class,
                () -> taskManager.deleteOneEpicSubTasks(taskId)
        );
        Assertions.assertEquals("Ошибка! Эпик с ID:" + taskId + " не найден", ex10.getMessage());
    }

    //Дополнительные тесты для подзадач когда EpicTaskId = null;
    @Test
    void shouldThrowExceptionWhenEpicTaskIdIsNull() {

        SubTask subTask = new SubTask("G", StatusType.NEW, "asdasd", null);
        subTask.setTaskId(1);
        NullPointerException ex1 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageSubTask(subTask)
        );
        Assertions.assertEquals("Ошибка при обработке Подзадачи! EpicTaskId = null", ex1.getMessage());

        NullPointerException ex2 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTaskObject(subTask)
        );
        Assertions.assertEquals("Ошибка при обработке Подзадачи! EpicTaskId = null", ex2.getMessage());
    }

    //Когда TaskId = null;
    @Test
    void shouldThrowExceptionWhenTaskIdIsNull() {
        NullPointerException ex1 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex1.getMessage());

        NullPointerException ex2 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getEpicTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex2.getMessage());

        NullPointerException ex3 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getSubTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex3.getMessage());

        NullPointerException ex4 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getTaskObjectById(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex4.getMessage());

        NullPointerException ex5 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteTaskObjectById(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex5.getMessage());

        NullPointerException ex6 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex6.getMessage());

        NullPointerException ex7 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteEpicTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex7.getMessage());

        NullPointerException ex8 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteSubTask(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex8.getMessage());

        NullPointerException ex9 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.getOneEpicSubTasks(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex9.getMessage());

        NullPointerException ex10 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.deleteOneEpicSubTasks(null)
        );
        Assertions.assertEquals("Ошибка! taskId = null", ex10.getMessage());
    }

    //Когда объект задачи = null
    @Test
    void shouldThrowExceptionWhenTaskIsNull() {
        NullPointerException ex1 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex1.getMessage());
        NullPointerException ex2 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageSubTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex2.getMessage());
        NullPointerException ex3 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageEpicTask(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex3.getMessage());
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex2.getMessage());
        NullPointerException ex4 = Assertions.assertThrows(
                NullPointerException.class,
                () -> taskManager.manageTaskObject(null)
        );
        Assertions.assertEquals("Ошибка при обработке задачи!" +
                " Невозможно обработать пустой объект задачи", ex4.getMessage());

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
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW);
        //Когда все подзадачи со статусом NEW
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW);
        //Когда есть подзадачи со статусами NEW и DONE
        testSubTask1.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS);
        //Когда все подзадачи со статусом DONE
        testSubTask1.setTaskStatus(StatusType.DONE);
        testSubTask2.setTaskStatus(StatusType.DONE);
        testSubTask3.setTaskStatus(StatusType.DONE);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.DONE);
        //Когда все подзадачи со статусом IN_PROGRESS
        testSubTask1.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask2.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask3.setTaskStatus(StatusType.IN_PROGRESS);
        taskManager.manageTaskObject(testSubTask1);
        taskManager.manageTaskObject(testSubTask2);
        taskManager.manageTaskObject(testSubTask3);
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS);
    }
    //С пустым списком задач, что бы это не значило...
    @Test
    void getAllShouldReturnEmptyList(){
        assertTrue(taskManager.getAllTasks().isEmpty());
        assertTrue(taskManager.getAllEpicTasks().isEmpty());
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getPrioritizedTasks().isEmpty());
        EpicTask epicTask = new EpicTask("test","gf");
        epicTask.setTaskId(1);
        taskManager.manageTaskObject(epicTask);
        assertTrue(taskManager.getAllSubTasks().isEmpty());
        assertTrue(taskManager.getOneEpicSubTasks(1).isEmpty());
    }
    //С пустым списком задач, что бы это не значило...
    @Test
    void deleteAllShouldReturnFalseIfTasksMapsIsEmpty(){

        assertFalse(taskManager.deleteAllEpicTasks());
        assertFalse(taskManager.deleteAllTasks());
        assertFalse(taskManager.deleteAllSubTasks());
        EpicTask epicTask = new EpicTask("test","gf");
        epicTask.setTaskId(1);
        taskManager.manageTaskObject(epicTask);
        assertFalse(taskManager.deleteAllSubTasks());
        assertFalse(taskManager.deleteOneEpicSubTasks(1));

    }

    @Test
    void manageSubTask() {
    }

    @Test
    void manageEpicTask() {
    }

    @Test
    void getTaskObjectById() {
    }

    @Test
    void getTask() {
    }

    @Test
    void getEpicTask() {
    }

    @Test
    void getSubTask() {
    }

    @Test
    void getOneEpicSubTasks() {
    }

    @Test
    void getAllTypesTasks() {
    }

    @Test
    void getPrioritizedTasks() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void getAllEpicTasks() {
    }

    @Test
    void getAllSubTasks() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void deleteTask() {
    }

    @Test
    void deleteEpicTask() {
    }

    @Test
    void deleteSubTask() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void deleteAllEpicTasks() {
    }

    @Test
    void deleteAllSubTasks() {
    }
}