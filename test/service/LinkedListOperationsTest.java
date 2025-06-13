package service;

import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinkedListOperationsTest {

    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testLinkedListOrder() {
        Task task1 = createTask(1, "Task 1");
        Task task2 = createTask(2, "Task 2");
        Task task3 = createTask(3, "Task 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    void testRemoveFromMiddle() {
        Task task1 = createTask(1, "Task 1");
        Task task2 = createTask(2, "Task 2");
        Task task3 = createTask(3, "Task 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void testDuplicateMovesToEnd() {
        Task task1 = createTask(1, "Task 1");
        Task task2 = createTask(2, "Task 2");
        Task task3 = createTask(3, "Task 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(task1, history.get(2));
    }

    private Task createTask(int id, String title) {
        Task task = new Task(title, "Description");
        task.setId(id);
        return task;
    }
}