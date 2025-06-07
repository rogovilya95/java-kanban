package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }
    }

    private final Map<Integer, Node> taskNodes = new HashMap<>();

    private final Node head = new Node(null);
    private final Node tail = new Node(null);

    public InMemoryHistoryManager() {
        head.next = tail;
        tail.prev = head;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        int taskId = task.getId();

        if (taskNodes.containsKey(taskId)) {
            removeNode(taskNodes.get(taskId));
        }

        Node newNode = linkLast(task);
        taskNodes.put(taskId, newNode);
    }

    @Override
    public void remove(int id) {
        Node nodeToRemove = taskNodes.get(id);
        if (nodeToRemove != null) {
            removeNode(nodeToRemove);
            taskNodes.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> history = new ArrayList<>();
        Node current = head.next;

        while (current != tail) {
            history.add(current.task);
            current = current.next;
        }

        return history;
    }

    private Node linkLast(Task task) {
        Node newNode = new Node(task);
        Node lastNode = tail.prev;

        lastNode.next = newNode;
        newNode.prev = lastNode;
        newNode.next = tail;
        tail.prev = newNode;

        return newNode;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
