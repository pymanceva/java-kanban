package ru.yandex.taskTracker.service;

public class Node<E> {
    protected E data;
    protected Node<E> next;
    protected Node<E> prev;

    public Node(Node<E> prev, E data, Node<E> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }
}
