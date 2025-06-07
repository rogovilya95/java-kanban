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
        Task customTask = new Task(999, "Custom Task", "Custom Description");
        Task savedCustomTask = taskManager.createTask(customTask);

        assertNotEquals(999, savedCustomTask.getId(), "Task should get a new auto-generated ID");

        savedCustomTask.setTitle("Updated Title");
        taskManager.updateTask(savedCustomTask);

        Task autoTask = taskManager.createTask(new Task("Auto Task", "Auto Description"));
        assertNotEquals(savedCustomTask.getId(), autoTask.getId(), "Custom and auto-generated ids should not conflict");

        assertEquals("Updated Title", taskManager.getTask(savedCustomTask.getId()).getTitle(),
                "Should retrieve the correct task with updated title");
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
    void testUnlimitedHistory() {
        for (int i = 0; i < 25; i++) {
            Task task = taskManager.createTask(new Task("Task " + i, "Description " + i));
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(25, history.size(), "History should be unlimited");

        assertEquals("Task 0", history.get(0).getTitle(), "First task in history should be Task 0");
        assertEquals("Task 24", history.get(24).getTitle(), "Last task in history should be Task 24");
    }

    @Test
    void testHistoryNoDuplicates() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2"));
        Epic epic = taskManager.createEpic(new Epic("Epic", "Epic Description"));

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getEpic(epic.getId());

        assertEquals(3, taskManager.getHistory().size());

        taskManager.getTask(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(3, history.size(), "History size should not increase on duplicate view");

        assertEquals(task2, history.get(0));
        assertEquals(epic, history.get(1));
        assertEquals(task1, history.get(2), "Duplicate task should move to end");
    }

    @Test
    void testHistoryAfterTaskDeletion() {
        Task task1 = taskManager.createTask(new Task("Task 1", "Description 1"));
        Task task2 = taskManager.createTask(new Task("Task 2", "Description 2"));

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());

        assertEquals(2, taskManager.getHistory().size());

        taskManager.deleteTask(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "History should remove deleted task");
        assertEquals(task2.getId(), history.get(0).getId());
    }

    @Test
    void testHistoryAfterEpicDeletion() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));
        Task task = taskManager.createTask(new Task("Task", "Description"));

        taskManager.getEpic(epic.getId());
        taskManager.getSubtask(subtask1.getId());
        taskManager.getSubtask(subtask2.getId());
        taskManager.getTask(task.getId());

        assertEquals(4, taskManager.getHistory().size());

        taskManager.deleteEpic(epic.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "Only regular task should remain in history");
        assertEquals(task.getId(), history.get(0).getId());
    }

    @Test
    void testHistoryManagerKeepsOriginalTaskState() {
        Task originalTask = taskManager.createTask(new Task("Original Title", "Original Description"));

        Task firstView = taskManager.getTask(originalTask.getId());

        Task updatedTask = new Task(originalTask.getId(), "Updated Title", "Updated Description");
        updatedTask.setStatus(Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        Task secondView = taskManager.getTask(originalTask.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "History should have 1 element (no duplicates)");

        Task taskInHistory = history.get(0);
        assertEquals("Updated Title", taskInHistory.getTitle(), "Task title in history should match the last viewed state");
        assertEquals("Updated Description", taskInHistory.getDescription(), "Task description in history should match the last viewed state");
        assertEquals(Status.IN_PROGRESS, taskInHistory.getStatus(), "Task status in history should match the last viewed state");

        firstView.setTitle("Should not affect manager");
        secondView.setDescription("Should not affect manager");

        Task freshCopy = taskManager.getTask(originalTask.getId());
        assertEquals("Updated Title", freshCopy.getTitle(), "Manager data should not be affected by external changes");
        assertEquals("Updated Description", freshCopy.getDescription(), "Manager data should not be affected by external changes");
    }
}