package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testEpicCannotBeItsOwnSubtask() {
        TaskManager taskManager = Managers.getDefault();

        // Создаем эпик
        Epic epic = new Epic("Test Epic", "Test Description");
        epic = taskManager.createEpic(epic);

        int epicId = epic.getId();

        // Пытаемся создать подзадачу с тем же id, что и у эпика
        Subtask subtask = new Subtask("Test Subtask", "Test Description", epicId);
        subtask.setId(epicId);

        // Проверяем, что обновление подзадачи выбросит исключение
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.updateSubtask(subtask);
        });

        String expectedMessage = "Subtask cannot be its own epic";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should contain: " + expectedMessage);
    }

    @Test
    void testSubtaskCannotBeItsOwnEpic() {
        TaskManager taskManager = Managers.getDefault();

        // Создаем эпик
        Epic epic = taskManager.createEpic(new Epic("Epic", "Description"));

        // Создаем подзадачу
        Subtask subtask = taskManager.createSubtask(new Subtask("Subtask", "Description", epic.getId()));

        // Пытаемся установить подзадаче её же id в качестве epicId
        subtask.setEpicId(subtask.getId());

        // Проверяем, что обновление такой подзадачи выбросит исключение
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            taskManager.updateSubtask(subtask);
        });

        String expectedMessage = "Subtask cannot be its own epic";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage),
                "Exception message should contain: " + expectedMessage);
    }

    @Test
    void testGetDefaultReturnsWorkingTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);

        Task task = taskManager.createTask(new Task("Test Task", "Description"));
        assertNotNull(task);
        assertTrue(task.getId() > 0);

        Epic epic = taskManager.createEpic(new Epic("Test Epic", "Description"));
        assertNotNull(epic);
        assertTrue(epic.getId() > 0);

        Subtask subtask = taskManager.createSubtask(new Subtask("Test Subtask", "Description", epic.getId()));
        assertNotNull(subtask);
        assertTrue(subtask.getId() > 0);
    }

    @Test
    void testGetDefaultHistoryReturnsWorkingHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager);

        Task task = new Task("Test", "Description");
        task.setId(1);

        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());

        historyManager.remove(1);
        assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void testManagersAreIndependent() {
        TaskManager manager1 = Managers.getDefault();
        TaskManager manager2 = Managers.getDefault();
        assertNotSame(manager1, manager2, "Different managers should be different objects");

        manager1.createTask(new Task("Task 1", "Description"));
        manager2.createTask(new Task("Task 2", "Description"));

        assertEquals(1, manager1.getAllTasks().size(), "Manager 1 should have 1 task");
        assertEquals(1, manager2.getAllTasks().size(), "Manager 2 should have 1 task");

        manager1.createTask(new Task("Another task", "Description"));
        assertEquals(2, manager1.getAllTasks().size(), "Manager 1 should have 2 tasks");
        assertEquals(1, manager2.getAllTasks().size(), "Manager 2 should still have 1 task");
    }
}