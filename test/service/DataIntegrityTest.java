package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataIntegrityTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void testEpicSubtaskIdsConsistencyAfterSubtaskDeletion() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(2, retrievedEpic.getSubtaskIds().size());
        assertTrue(retrievedEpic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(retrievedEpic.getSubtaskIds().contains(subtask2.getId()));

        taskManager.deleteSubtask(subtask1.getId());

        retrievedEpic = taskManager.getEpic(epic.getId());
        assertEquals(1, retrievedEpic.getSubtaskIds().size());
        assertFalse(retrievedEpic.getSubtaskIds().contains(subtask1.getId()));
        assertTrue(retrievedEpic.getSubtaskIds().contains(subtask2.getId()));
    }

    @Test
    void testSubtaskEpicIdConsistencyAfterEpicChange() {
        Epic epic1 = taskManager.createEpic(new Epic("Epic 1", "Description"));
        Epic epic2 = taskManager.createEpic(new Epic("Epic 2", "Description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic1.getId()));

        assertTrue(taskManager.getEpic(epic1.getId()).getSubtaskIds().contains(subtask.getId()));
        assertFalse(taskManager.getEpic(epic2.getId()).getSubtaskIds().contains(subtask.getId()));

        Subtask updatedSubtask = new Subtask("Subtask", "Description", epic2.getId());
        updatedSubtask.setId(subtask.getId());
        updatedSubtask.setStatus(subtask.getStatus());
        taskManager.updateSubtask(updatedSubtask);

        assertFalse(taskManager.getEpic(epic1.getId()).getSubtaskIds().contains(subtask.getId()));
        assertTrue(taskManager.getEpic(epic2.getId()).getSubtaskIds().contains(subtask.getId()));
    }

    @Test
    void testTaskFieldModificationDoesNotAffectManager() {
        // Создаем задачу
        Task createdTask = taskManager.createTask(new Task("Original Title", "Original Description"));
        int taskId = createdTask.getId();
        String originalTitle = createdTask.getTitle();
        String originalDescription = createdTask.getDescription();
        Status originalStatus = createdTask.getStatus();

        createdTask.setTitle("Modified Title");
        createdTask.setDescription("Modified Description");
        createdTask.setStatus(Status.DONE);

        Task retrievedTask = taskManager.getTask(taskId);
        assertEquals(originalTitle, retrievedTask.getTitle(), "Title should not be affected by external modification");
        assertEquals(originalDescription, retrievedTask.getDescription(), "Description should not be affected by external modification");
        assertEquals(originalStatus, retrievedTask.getStatus(), "Status should not be affected by external modification");

        retrievedTask.setTitle("Another Modified Title");
        retrievedTask.setDescription("Another Modified Description");
        retrievedTask.setStatus(Status.IN_PROGRESS);

        Task thirdRetrieve = taskManager.getTask(taskId);
        assertEquals(originalTitle, thirdRetrieve.getTitle(), "Title should still not be affected");
        assertEquals(originalDescription, thirdRetrieve.getDescription(), "Description should still not be affected");
        assertEquals(originalStatus, thirdRetrieve.getStatus(), "Status should still not be affected");
    }

    @Test
    void testEpicSubtaskIdsListModificationSafety() {
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic.getId()));

        Epic retrievedEpic = taskManager.getEpic(epic.getId());
        List<Integer> subtaskIds = retrievedEpic.getSubtaskIds();
        int originalSize = subtaskIds.size();

        try {
            subtaskIds.add(999);
            subtaskIds.clear();
        } catch (UnsupportedOperationException e) {
            // Это ожидаемое исключение, если список защищен от изменений
        }

        Epic freshEpic = taskManager.getEpic(epic.getId());
        assertEquals(originalSize, freshEpic.getSubtaskIds().size(), "Epic subtask list size should not be affected by external modifications");
        assertTrue(freshEpic.getSubtaskIds().contains(subtask.getId()), "Epic should still contain the original subtask");
    }
}