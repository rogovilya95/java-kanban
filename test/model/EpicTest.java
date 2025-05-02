package model;

import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void testEpicCannotBeItsOwnSubtask() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = new Epic("Test Epic", "Test Description");
        epic = taskManager.createEpic(epic);
        int epicId = epic.getId();

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epicId);
        subtask.setId(epicId);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {taskManager.updateSubtask(subtask);});

        String expectedMessage = "Subtask cannot be its own epic";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should contain: " + expectedMessage);
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic.getId()));

        subtask.setEpicId(subtask.getId());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {taskManager.updateSubtask(subtask);});

        String expectedMessage = "Subtask cannot be its own epic";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should contain: " + expectedMessage);
    }

    @Test
    void testEpicStatusWithNoSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Epic without subtasks", "Description"));
        assertEquals(Status.NEW, epic.getStatus(), "Epic with no subtasks should have NEW status");
    }

    @Test
    void testEpicStatusWithAllNewSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));

        assertEquals(Status.NEW, taskManager.getEpic(epic.getId()).getStatus(),
                "Epic with all NEW subtasks should have NEW status");
    }

    @Test
    void testEpicStatusWithAllDoneSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));

        // Mark all subtasks as DONE
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, taskManager.getEpic(epic.getId()).getStatus(),
                "Epic with all DONE subtasks should have DONE status");
    }

    @Test
    void testEpicStatusWithMixedSubtasks() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));
        Subtask subtask1 = taskManager.createSubtask(new Subtask("Subtask 1", "Description", epic.getId()));
        Subtask subtask2 = taskManager.createSubtask(new Subtask("Subtask 2", "Description", epic.getId()));

        // Set mixed statuses
        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);
        subtask2.setStatus(Status.NEW);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus(),
                "Epic with mixed status subtasks should have IN_PROGRESS status");
    }
}