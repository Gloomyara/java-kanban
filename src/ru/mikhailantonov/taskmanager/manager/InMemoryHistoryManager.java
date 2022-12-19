package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Класс для хранения истории просмотров задач, через методы getTaskByID
 */

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> utilMap = new HashMap<>();
    /**
     * Указатель на первый элемент списка. Он же first
     */
    private Node<Task> head;

    /**
     * Указатель на последний элемент списка. Он же last
     */
    private Node<Task> tail;
    /**
     * Размер несуществующего листа
     */
    private int size = 0;

    @Override
    public void add(Task task) {
        int taskId = task.getTaskId();
        if (utilMap.containsKey(taskId)) {
            removeNode(utilMap.get(taskId));
        }
        linkLast(task);
        utilMap.put(taskId, tail);
    }

    @Override
    public void remove(int taskId) {
        removeNode(utilMap.get(taskId));
        utilMap.remove(taskId);
    }

    @Override
    public List<Task> getHistory() {
        if (getTasks() != null) {
            return getTasks();
        } else {
            System.out.println("Ошибка! Нет задач");
            return null;
        }
    }

    private void linkLast(Task element) {

        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        size++;
    }

    private int size() {
        return this.size;
    }

    private void removeNode(Node<Task> node) {

        if (node.prev == null) {
            if (node.next != null) {
                Node<Task> nextTask = node.next;
                nextTask.prev = null;
                head = nextTask;
            }
        } else if (node.next == null) {
            Node<Task> prevTask = node.prev;
            prevTask.next = null;
            tail = prevTask;
        } else {
            Node<Task> prevTask = node.prev;
            Node<Task> nextTask = node.next;
            prevTask.next = nextTask;
            nextTask.prev = prevTask;
        }
        node = null;
        size--;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> utilList = new ArrayList<>();

        if (head == null) {
            return null;
        }
        Node<Task> task = head;
        utilList.add(task.value);
        for (int i = 1; i < size(); i++) {
            if (task.next != null) {
                Node<Task> nextTask = task.next;
                utilList.add(nextTask.value);
                task = nextTask;
            }
        }
        return utilList;
    }
}

