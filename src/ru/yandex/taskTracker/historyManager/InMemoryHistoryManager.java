package ru.yandex.taskTracker.historyManager;

import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Node;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    private static final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private static final Map<Integer, Node<Task>> historyMap = new LinkedHashMap<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            Node<Task> newNode;
            if (historyMap.containsKey(task.getId())) {
                newNode = historyMap.get(task.getId());
                history.removeNode(newNode);
                history.linkLast(task);
                historyMap.remove(task.getId());
            } else {
                newNode = history.linkLast(task);
            }
            historyMap.put(task.getId(), newNode);
        }
    }

    public void clearHistory() {
        for (Integer id : historyMap.keySet()) {
            history.removeNode(historyMap.get(id));
        }
        historyMap.clear();

    }

    @Override
    public List<Task> getTasks() {
        return history.tasksList();
    }

    public CustomLinkedList<Task> getHistory() {
        return history;
    }

    @Override
    public void removeTask(int id) {
        if (historyMap.containsKey(id)) {
            history.removeNode(historyMap.get(id));
            historyMap.remove(id);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (Integer id : historyMap.keySet()) {
            str.append(id).append(",");
        }
        return str.toString();
    }
}

class CustomLinkedList<E> {
    private static int size = 0;
    private Node<E> head;
    private Node<E> tail;

    public Node<E> getHead() {
        return head;
    }

    public Node<E> getTail() {
        return tail;
    }

    public Node<E> linkLast(E task) {
        Node<E> oldTail = tail;
        Node<E> newNode = new Node<>(oldTail, task, null);
        if (task != null) {
            tail = newNode;
            if (oldTail == null)
                head = newNode;
            else
                oldTail.next = newNode;
            size++;
        } else {
            newNode = tail;
        }
        return newNode;
    }

    public void removeNode(Node<E> x) {
        if (x != null) {
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
        }
    }

    public List<E> tasksList() {
        List<E> result = new ArrayList<>();
        for (Node<E> x = head; x != null; x = x.next) {
            result.add(x.data);
        }
        return result;
    }

    public int size() {
        return size;
    }
}

