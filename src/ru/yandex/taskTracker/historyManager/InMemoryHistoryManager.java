package ru.yandex.taskTracker.historyManager;

import ru.yandex.taskTracker.model.Task;
import ru.yandex.taskTracker.util.Node;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static final CustomLinkedList<Task> history = new CustomLinkedList<>();
    private static final Map<Integer, Node<Task>> historyMap = new LinkedHashMap<>();

    @Override
    public void addTask(Task task) {
        if (task != null) {
            if (historyMap.containsKey(task.getId())) {
                Node<Task> newNode = historyMap.get(task.getId());
                history.removeNode(newNode);
                history.linkLast(task);
                historyMap.remove(task.getId());
                historyMap.put(task.getId(), newNode);
            } else {
                Node<Task> newNode = history.linkLast(task);
                historyMap.put(task.getId(), newNode);
            }
        }
    }

    public void clearHistory() {
        historyMap.clear();

    }

    @Override
    public List<Task> getTasks() {
        return history.tasksList();
    }

    @Override
    public void removeTask(int id) {
        history.removeNode(historyMap.get(id));
        historyMap.remove(id);
    }

    @Override
    public String toString() {
        String str = "";
        for (Integer id :historyMap.keySet()) {
            str = str + id + ",";
        }
        return str;
    }
}

    class CustomLinkedList<E>{
        private Node<E> head;
        private Node<E> tail;
        private static int size = 0;

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

        public List<E> tasksList () {
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

