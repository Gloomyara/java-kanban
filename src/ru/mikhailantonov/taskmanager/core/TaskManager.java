package ru.mikhailantonov.taskmanager.core;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

public class TaskManager {

    private HashMap<Integer, Task> epicTaskList = new HashMap<>();

    private HashMap<Integer, Task> subTaskList = new HashMap<>();
    private HashMap<Integer, Task> taskList = new HashMap<>();
    private HashMap<Integer, Integer> subTaskId =  new HashMap<>();

    Scanner scanner = new Scanner(System.in);

    public void createTask() {
        int id = 0;
        while (true) {

            System.out.print("Какую задачу вы хотите создать?" + "\n 1 - Создать задачу" + "\n 2/3 - Создать/редактировать эпик");
            int option = Integer.parseInt(scanner.nextLine());

            id++;

            if (option == 1) {

                System.out.print("\nНазвание задачи -->");
                String taskName = scanner.nextLine();
                System.out.print("\nОписание задачи -->");
                String taskDescription = scanner.nextLine();
                if (!taskList.containsKey(id)) {
                    taskList.put(id, new Task(id, taskName, taskDescription));
                }

            } else if (option == 2) {
                if (!taskList.containsKey(id)) {

                    System.out.print("\nНазвание задачи -->");
                    String taskName = scanner.nextLine();
                    System.out.print("\nОписание задачи -->");
                    String taskDescription = scanner.nextLine();

                    taskList.put(id, new EpicTask(id, taskName, taskDescription));
                    EpicTask oneTask = (EpicTask) taskList.get(id);
                    //oneTask.setTaskStatus(oneTask.epicStatusType());

                }
            } else if (option == 3) {
                System.out.println("Создать подзадачу");
                System.out.print("\nВведите ID эпика -->");
                int taskId = Integer.parseInt(scanner.nextLine());

                if (taskList.containsKey(taskId)) {
                    EpicTask oneTask = (EpicTask) taskList.get(taskId);
                    System.out.println("Эпик " + oneTask.getTaskName() + "на связи");
                    System.out.print("\nНазвание задачи -->");
                    String taskName = scanner.nextLine();

                    System.out.print("\nОписание задачи -->");
                    String taskDescription = scanner.nextLine();
                    oneTask.addSubTask(id, taskName, taskDescription);
                    subTaskId.put(id, taskId);
                } else {
                    System.out.println("такой задачи нет.");
                    id--;
                }

            } else if (option == 0) {
                break;
            } else {
                System.out.println("такой команды, пока нет.");
                id--;
            }
        }
    }
    public void appointTask() {

        System.out.println("Введите ID задачи, которую хотите взять в работу");
        int id = Integer.parseInt(scanner.nextLine());

        if (subTaskId.containsKey(id)){

            EpicTask oneTask = (EpicTask) taskList.get(subTaskId.get(id));
            SubTask subTask = oneTask.getSubTasks().get(id);

            subTask.taskStatus = StatusType.IN_PROGRESS;
            subTask.setTaskUpdateDate(Calendar.getInstance());
            //subTask.setTaskAppoint();
            oneTask.taskStatus = oneTask.epicStatusType();

        } else if (taskList.containsKey(id)){

            Task oneTask = taskList.get(id);

            oneTask.taskStatus = StatusType.IN_PROGRESS;
            oneTask.setTaskUpdateDate(Calendar.getInstance());
            //oneTask.setTaskAppoint();
        }

    }
    public String deleteOneTask(int id) {
        if (subTaskId.containsKey(id)){
            EpicTask oneTask = (EpicTask) taskList.get(subTaskId.get(id));
            oneTask.deleteOneTask(id);
            return "Задача удалена.";
        } else if (taskList.containsKey(id)) {
            taskList.remove(id);
            return "Задача удалена.";
        }
        return "Задачи под таким ID нет.";
    }
    public void printAllTasks() {
        if (taskList != null) {
            for (Task oneTask : taskList.values()) {
                System.out.println(oneTask.taskName);
                oneTask.printAllTasks();
            }
        } else {
            System.out.println("В списке нет задач");
        }
    }
}
