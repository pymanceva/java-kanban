package ru.yandex.taskTracker.service;

import ru.yandex.taskTracker.model.Task;
import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private static final Map<Integer, Node<Task>> historyMap = new HashMap<>();

    @Override
    public void addTask(Task task) {
        if (historyMap.containsKey(task.getId())) {
            Node<Task> newNode = historyMap.get(task.getId());
            history.removeNode(newNode);
            history.linkLast(task);
            historyMap.put(task.getId(), newNode);
        } else {
            Node<Task> newNode = history.linkLast(task);
            historyMap.put(task.getId(), newNode);
        }
    }

    @Override
    public ArrayList<Task> getTasks() {
        return history.tasksList();
    }

    @Override
    public void removeTask(int id) {
        history.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }
}

    class CustomLinkedList<E>{
        private Node<E> head;
        private Node<E> tail;
        private static int size = 0;

        public Node<E> linkLast(E task) {
            final Node<E> oldTail = tail;
            final Node<E> newNode = new Node<>(oldTail, task, null);
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
            return newNode;
        }

        public E removeNode(Node<E> x) {
            final E element = x.data;
            final Node<E> next = x.next;
            final Node<E> prev = x.prev;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                x.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                x.next = null;
            }

            x.data = null;
            CustomLinkedList.size--;
            return element;
        }

        public ArrayList<E> tasksList () {
            ArrayList<E> result = new ArrayList<>();
            for (Node<E> x = head; x != null; x = x.next) {
                result.add(x.data);
            }
            return result;
        }

        public int size() {
            return size;
        }
    }

