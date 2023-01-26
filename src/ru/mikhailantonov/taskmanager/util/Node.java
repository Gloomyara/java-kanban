package ru.mikhailantonov.taskmanager.util;

/**
 * Utility class для handmade linked-list в менеджере HistoryManager
 * @param <E>
 */
public class Node<E> {
    private E value;
    private Node<E> next;

    private Node<E> prev;
    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    public Node<E> getPrev() {
        return prev;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }



    public Node(Node<E> prev, E value, Node<E> next) {
        this.prev = prev;
        this.value = value;
        this.next = next;

    }
}