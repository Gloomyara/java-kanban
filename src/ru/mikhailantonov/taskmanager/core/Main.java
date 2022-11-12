package ru.mikhailantonov.taskmanager.core;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        Scanner scanner = new Scanner(System.in);
        TaskManager taskManager = new TaskManager();

        while (true) {
            printMenu();
            try { //обработка ввода
                int command = Integer.parseInt(scanner.nextLine());

                if (command == 1) {

                } else if (command == 2) {

                } else if (command == 3) {

                } else if (command == 4) {

                } else if (command == 5) {

                } else if (command == 0) {
                    System.out.println("Выход");
                    break;
                } else {
                    System.out.println("Извините, такой команды пока нет.");
                }
            } catch (Exception e) {
                System.out.println("Ошибка! введите число");
            }
        }
    }

    public static void printMenu() {

        System.out.println("Что вы хотите сделать? ");
        System.out.println("1 - Возможность хранить задачи всех типов. Для этого вам нужно выбрать подходящую коллекцию.");
        System.out.println("2 - Методы для каждого из типа задач(Задача/Эпик/Подзадача):");
        System.out.println("2.1 - Получение списка всех задач.");
        System.out.println("2.2 - Удаление всех задач.");
        System.out.println("2.3 - Получение по идентификатору.");
        System.out.println("2.4 - Создание. Сам объект должен передаваться в качестве параметра.");
        System.out.println("2.5 - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.");
        System.out.println("2.6 - Удаление по идентификатору.");

        System.out.println("3 - Дополнительные методы:");
        System.out.println("3.1 - Получение списка всех подзадач определённого эпика.");

        System.out.println("4 - Управление статусами осуществляется по следующему правилу:");
        System.out.println("4.1 - Менеджер сам не выбирает статус для задачи." +
                " Информация о нём приходит менеджеру вместе с информацией о самой задаче." +
                " По этим данным в одних случаях он будет сохранять статус, в других будет рассчитывать.");

        System.out.println("5 - Для эпиков: ");
        System.out.println("5.1 - если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.");
        System.out.println("5.2 - если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE.");
        System.out.println("5.3 - во всех остальных случаях статус должен быть IN_PROGRESS.");

        System.out.println("0 - Выход");

    }
}

