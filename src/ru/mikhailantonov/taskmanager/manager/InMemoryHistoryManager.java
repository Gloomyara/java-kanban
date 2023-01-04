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
        return getTasks();
    }

    private void linkLast(Task element) {

        final Node<Task> oldTail = tail;
        final Node<Task> newNode = new Node<>(tail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.setNext(newNode);
        }
        size++;
    }

    private int size() {
        return this.size;
    }

    private void removeNode(Node<Task> node) {

        if (node.getPrev() == null) {
            if (node.getNext() != null) {
                node.getNext().setPrev(null);
                head = node.getNext();
            }
        } else if (node.getNext() == null) {
            node.getPrev().setNext(null);
            tail = node.getPrev();
        } else {
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }
        size--;
    }

    private ArrayList<Task> getTasks() {
        ArrayList<Task> utilList = new ArrayList<>();

        if (head == null) {
            return utilList;
        }
        Node<Task> task = head;
        utilList.add(task.getValue());
        while (task.getNext() != null) {
            task = task.getNext();
            utilList.add(task.getValue());
        }
        return utilList;
    }
}

