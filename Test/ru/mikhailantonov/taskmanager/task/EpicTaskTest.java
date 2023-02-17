package ru.mikhailantonov.taskmanager.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.mikhailantonov.taskmanager.task.enums.StatusType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTaskTest {
    EpicTask testEpicTask;
    SubTask testSubTask1;
    SubTask testSubTask2;
    SubTask testSubTask3;

    @BeforeEach
    void createNewEpicTaskAndSomeSubTasks() {
        testEpicTask = new EpicTask("Эпик1", "aa");
        testEpicTask.setTaskId(1);
        testSubTask1 = new SubTask("Э1 ПодЗадача1", StatusType.NEW, "asdfsa", 1);
        testSubTask1.setTaskId(2);
        testSubTask2 = new SubTask("Э1 ПодЗадача2", StatusType.NEW, "fdfasdf", 1);
        testSubTask2.setTaskId(3);
        testSubTask3 = new SubTask("Э1 ПодЗадача3", StatusType.NEW, "asdf", 1);
        testSubTask3.setTaskId(4);
    }

    //Когда нет подзадач
    @Test
    void epicStatusTypeShouldBeNewWhenSubTaskMapIsEmpty() {
        assertTrue(testEpicTask.getSubTaskMap().isEmpty());
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW);
    }

    //Когда все подзадачи со статусом NEW
    @Test
    void epicStatusTypeShouldBeNewWhenAllSubTaskStatusTypeNew() {
        testEpicTask.getSubTaskMap().put(2, testSubTask1);
        testEpicTask.getSubTaskMap().put(3, testSubTask2);
        testEpicTask.getSubTaskMap().put(4, testSubTask3);
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.NEW);
    }

    //Когда все подзадачи со статусом DONE
    @Test
    void epicStatusTypeShouldBeDoneWhenAllSubTaskStatusTypeDone() {
        testSubTask1.setTaskStatus(StatusType.DONE);
        testSubTask2.setTaskStatus(StatusType.DONE);
        testSubTask3.setTaskStatus(StatusType.DONE);
        testEpicTask.getSubTaskMap().put(2, testSubTask1);
        testEpicTask.getSubTaskMap().put(3, testSubTask2);
        testEpicTask.getSubTaskMap().put(4, testSubTask3);
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.DONE);
    }

    //Когда есть подзадачи со статусами NEW и DONE
    @Test
    void epicStatusTypeShouldBeInProgressWhenSomeSubTaskStatusTypeNewAndStatusTypeDone() {
        testSubTask1.setTaskStatus(StatusType.DONE);
        testEpicTask.getSubTaskMap().put(2, testSubTask1);
        testEpicTask.getSubTaskMap().put(3, testSubTask2);
        testEpicTask.getSubTaskMap().put(4, testSubTask3);
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS);
    }

    //Когда все подзадачи со статусом IN_PROGRESS
    @Test
    void epicStatusTypeShouldBeInProgressWhenAllSubTaskStatusTypeInProgress() {
        testSubTask1.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask2.setTaskStatus(StatusType.IN_PROGRESS);
        testSubTask3.setTaskStatus(StatusType.IN_PROGRESS);
        testEpicTask.getSubTaskMap().put(2, testSubTask1);
        testEpicTask.getSubTaskMap().put(3, testSubTask2);
        testEpicTask.getSubTaskMap().put(4, testSubTask3);
        testEpicTask.setTaskStatus(testEpicTask.epicStatusType());
        assertEquals(testEpicTask.getTaskStatus(), StatusType.IN_PROGRESS);
    }
}