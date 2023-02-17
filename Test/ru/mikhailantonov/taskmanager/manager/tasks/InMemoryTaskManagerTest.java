package ru.mikhailantonov.taskmanager.manager.tasks;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void createManager() {
        taskManager = new InMemoryTaskManager();
    }
}