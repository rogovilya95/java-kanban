package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void testUnlimitedHistorySize() {
        for (int i = 0; i < 50; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            task.setId(i + 1);
            historyManager.add(task);
        }

        List<Task> history = historyManager.getHistory();
        assertEquals(50, history.size(), "History should be unlimited");

        assertEquals("Task 0", history.get(0).getTitle());
        assertEquals("Task 49", history.get(49).getTitle());
    }

    @Test
    void testNoDuplicatesInHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size(), "History size should remain 3");

        assertEquals(task2, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(task1, history.get(2));
    }

    @Test
    void testRemoveFromHistory() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size());

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
    }

    @Test
    void testRemoveNonExistentTask() {
        Task task = new Task("Task", "Description");
        task.setId(1);
        historyManager.add(task);

        historyManager.remove(999);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void testAddNullTask() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty(), "Null task should not be added to history");
    }

    @Test
    void testEmptyHistory() {
        assertTrue(historyManager.getHistory().isEmpty(), "New history should be empty");
    }

    @Test
    void testMultipleViewsOfSameTask() {
        Task task = new Task("Task", "Description");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Task should be in history only once");
        assertEquals(task, history.get(0));
    }

    @Test
    void testTaskManagerHistoryIntegration() {
        Task task = taskManager.createTask(new Task("Task", "Description"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic.getId()));

        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size());

        taskManager.deleteTask(task.getId());
        history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.stream().anyMatch(t -> t.getId() == epic.getId()));
        assertTrue(history.stream().anyMatch(t -> t.getId() == subtask.getId()));
    }

    @Test
    void testEpicDeletionRemovesSubtasksFromHistory() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));

        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());

        assertEquals(3, taskManager.getHistory().size());

        taskManager.deleteEpic(epic.getId());

        assertTrue(taskManager.getHistory().isEmpty());
    }

    @Test
    void testOrderPreservationAfterDuplicateView() {
        Task task1 = new Task("Task 1", "Description 1");
        task1.setId(1);
        Task task2 = new Task("Task 2", "Description 2");
        task2.setId(2);
        Task task3 = new Task("Task 3", "Description 3");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.add(task2);

        List<Task> history = historyManager.getHistory();

        assertEquals(task1, history.get(0));
        assertEquals(task3, history.get(1));
        assertEquals(task2, history.get(2));
    }
}
