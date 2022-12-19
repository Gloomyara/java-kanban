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
                Node<Task> next = node.next;
                next.prev = null;
                head = next;
            }
        } else if (node.next == null) {
            Node<Task> prev = node.prev;
            prev.next = null;
            tail = prev;
        } else {
            Node<Task> prev = node.prev;
            Node<Task> next = node.next;
            prev.next = next;
            next.prev = prev;
        }
        node.data = null;
        node.next = null;
        node.prev = null;
        node = null;
        size--;
    }

    private ArrayList<Task> getTasks() {

        ArrayList<Task> utilList = new ArrayList<>();

        if (head != null) {
            Node<Task> t = head;
            utilList.add(t.data);
            for (int i = 1; i < size(); i++) {
                if (t.next != null) {
                    Node<Task> n = t.next;
                    utilList.add(n.data);
                    t = n;
                }
            }
            return utilList;
        } else {
            return null;
        }
    }
}

