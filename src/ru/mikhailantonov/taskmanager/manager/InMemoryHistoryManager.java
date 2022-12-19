package ru.mikhailantonov.taskmanager.manager;

import ru.mikhailantonov.taskmanager.task.*;
import ru.mikhailantonov.taskmanager.util.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Класс для хранения истории просмотров задач, через методы getTaskByID
 */

public class InMemoryHistoryManager implements HistoryManager {

    private final HashMap<Integer, Node<Task>> utilMap = new HashMap<>();
    private final HandMadeLinkedList<Task> taskRequestHistory = new HandMadeLinkedList<>();

    @Override
    public void add(Task task) {
        int taskId = task.getTaskId();
        if (utilMap.containsKey(taskId)) {
            taskRequestHistory.removeNode(utilMap.get(taskId));
        }
        taskRequestHistory.linkLast(task);
        utilMap.put(taskId, taskRequestHistory.tail);
    }

    @Override
    public void remove(int taskId) {
        taskRequestHistory.removeNode(utilMap.get(taskId));
        utilMap.remove(taskId);
    }

    @Override
    public List<Task> getHistory() {
        if (taskRequestHistory.getTasks() != null) {
            return taskRequestHistory.getTasks();
        } else {
            System.out.println("Ошибка! Нет задач");
            return null;
        }
    }

    public static class HandMadeLinkedList<T> {

        /**
         * Указатель на первый элемент списка. Он же first
         */
        private Node<T> head;

        /**
         * Указатель на последний элемент списка. Он же last
         */
        private Node<T> tail;

        private int size = 0;

        public void addFirst(T element) {
            final Node<T> oldHead = head;
            final Node<T> newNode = new Node<>(null, element, oldHead);
            head = newNode;
            if (oldHead == null) {
                tail = newNode;
            } else {
                oldHead.prev = newNode;
            }
            size++;
        }

        public T getFirst() {
            final Node<T> curHead = head;
            if (curHead == null) {
                throw new NoSuchElementException();
            }
            return head.data;
        }

        public void linkLast(T element) {

            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(tail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
            size++;
        }

        public T getLast() {

            final Node<T> curTail = tail;
            if (curTail == null) {
                throw new NoSuchElementException();
            }
            return tail.data;
        }

        public int size() {
            return this.size;
        }

        public void removeNode(Node<T> node) {

            if (node.prev == null) {
                if (node.next != null) {
                    Node<T> next = node.next;
                    next.prev = null;
                    head = next;
                }
            } else if (node.next == null) {
                Node<T> prev = node.prev;
                prev.next = null;
                tail = prev;
            } else {
                Node<T> prev = node.prev;
                Node<T> next = node.next;
                prev.next = next;
                next.prev = prev;
            }
            node.data = null;
            node.next = null;
            node.prev = null;
            size--;
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> utilList = new ArrayList<>();
            Node<T> t = head;
            if (t.data != null) {
                utilList.add(t.data);
                for (int i = 1; i < size(); i++) {
                    if (t.next != null) {
                        Node<T> n = t.next;
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
}
