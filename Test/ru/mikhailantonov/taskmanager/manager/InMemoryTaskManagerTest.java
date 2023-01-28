package ru.mikhailantonov.taskmanager.manager;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void createManager(){
        taskManager = new InMemoryTaskManager();
    }
}