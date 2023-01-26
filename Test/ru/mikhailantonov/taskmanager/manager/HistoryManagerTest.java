package ru.mikhailantonov.taskmanager.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.task.Task;
import ru.mikhailantonov.taskmanager.util.StatusType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    HistoryManager historyManager;
    Task task1;
    Task task2;
    Task task3;

    @BeforeEach
    void createNewHistoryManagerAndSomeTasks() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task("Задача1", StatusType.NEW, "adsf");
        task1.setTaskId(1);
        task2 = new Task("Задача2", StatusType.NEW, "adssf");
        task2.setTaskId(2);
        task3 = new Task("Задача3", StatusType.NEW, "adfsf");
        task3.setTaskId(3);
    }

    @Test
    void addCheck() {
        historyManager.add(task1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История не пустая.");
    }

    //удаление последнего элемента
    @Test
    void newTailShouldBeEqualToThePrevWhenTailRemoved() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(task2, history.get(history.size() - 1));
    }

    //удаление первого элемента
    @Test
    void newHeadShouldBeEqualToTheNextWhenHeadRemoved() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(task2, history.get(0));
    }

    //удаление из середины списка
    @Test
    void removeMidElementCheck() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(2, history.size(), "элемент удален");
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    //Дублирование
    @Test
    void sameTaskShouldBeUniqueAndEqualsTail() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        final List<Task> history = historyManager.getHistory();
        assertNotNull(history, "История не пустая.");
        assertEquals(3, history.size(), "Элементы не дублируются");
        assertEquals(task1, history.get(history.size() - 1));
    }

    //пустая история задач
    @Test
    void shouldThrowExceptionWhenHistoryIsEmpty() {
        NullPointerException ex = Assertions.assertThrows(
                NullPointerException.class,
                () -> historyManager.getHistory()
        );
        Assertions.assertEquals("Ошибка! Список истории задач пуст", ex.getMessage());
    }
}
