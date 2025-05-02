package service;

import model.Epic;
import model.Subtask;
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
}