package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testAddDifferentTypesOfTasks() {
        Task task = taskManager.createTask(new Task("Task", "Task Description"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic Description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Subtask Description", epic.getId()));

        assertEquals(task, taskManager.getTask(task.getId()), "Should retrieve the same task by id");
        assertEquals(epic, taskManager.getEpic(epic.getId()), "Should retrieve the same epic by id");
        assertEquals(subtask, taskManager.getSubtask(subtask.getId()), "Should retrieve the same subtask by id");

        assertFalse(taskManager.getAllTasks().isEmpty(), "Tasks list should not be empty");
        assertFalse(taskManager.getAllEpics().isEmpty(), "Epics list should not be empty");
        assertFalse(taskManager.getAllSubtasks().isEmpty(), "Subtasks list should not be empty");
    }

    @Test
    void testTasksWithCustomAndGeneratedIdsDontConflict() {
        Task customTask = new Task("Custom Task", "Custom Description");
        customTask.setId(999);
        taskManager.createTask(customTask);
        customTask.setTitle("Updated Title");
        taskManager.updateTask(customTask);


        Task autoTask = taskManager.createTask(new Task("Auto Task", "Auto Description"));
        assertNotEquals(customTask.getId(), autoTask.getId(), "Custom and auto-generated ids should not conflict");

        assertEquals(customTask.getTitle(), taskManager.getTask(customTask.getId()).getTitle(),
                "Should retrieve the correct task with custom id");
        assertEquals(autoTask.getTitle(), taskManager.getTask(autoTask.getId()).getTitle(),
                "Should retrieve the correct task with auto-generated id");
    }

    @Test
    void testTaskFieldsRemainUnchangedWhenAddedToManager() {
        Task originalTask = new Task("Original Title", "Original Description");
        originalTask.setStatus(Status.IN_PROGRESS);
        Task expectedTask = new Task(originalTask.getTitle(), originalTask.getDescription());
        expectedTask.setStatus(originalTask.getStatus());
        Task savedTask = taskManager.createTask(originalTask);

        assertEquals(expectedTask.getTitle(), savedTask.getTitle(), "Title should remain unchanged");
        assertEquals(expectedTask.getDescription(), savedTask.getDescription(), "Description should remain unchanged");
        assertEquals(expectedTask.getStatus(), savedTask.getStatus(), "Status should remain unchanged");
    }

    @Test
    void testHistory() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic Description"));

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "History size should be 3");
        assertEquals(task1, history.get(0), "First viewed task should be first in history");
        assertEquals(task2, history.get(1), "Second viewed task should be second in history");
        assertEquals(epic, history.get(2), "Third viewed task should be third in history");
    }

    @Test
    void testHistoryLimitedToTenTasks() {
        for (int i = 0; i < 15; i++) {
            Task task = taskManager.createTask(new Task("Task " + i, "Description " + i));
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();

        assertEquals(10, history.size(), "History should be limited to 10 tasks");

        assertEquals("Task 5", history.get(0).getTitle(), "First task in history should be Task 5");
        assertEquals("Task 14", history.get(9).getTitle(), "Last task in history should be Task 14");
    }

    @Test
    void testHistoryManagerKeepsOriginalTaskState() {
        Task originalTask = taskManager.createTask(new Task("Original Title", "Original Description"));
        String originalTitle = originalTask.getTitle();
        String originalDescription = originalTask.getDescription();
        Status originalStatus = originalTask.getStatus();

        taskManager.getTask(originalTask.getId());
        Task updatedTask = new Task(originalTask.getId(), "Updated Title", "Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task retrievedTask = taskManager.getTask(originalTask.getId());
        assertEquals("Updated Title", retrievedTask.getTitle());
        assertEquals("Updated Description", retrievedTask.getDescription());
        assertEquals(Status.IN_PROGRESS, retrievedTask.getStatus());

        List<Task> history = taskManager.getHistory();

        assertFalse(history.isEmpty(), "History should not be empty");
        Task taskInHistory = history.getFirst();
        assertEquals(originalTitle, taskInHistory.getTitle(), "Task title in history should match the original");
        assertEquals(originalDescription, taskInHistory.getDescription(), "Task description in history should match the original");
        assertEquals(originalStatus, taskInHistory.getStatus(), "Task status in history should match the original");
    }
}