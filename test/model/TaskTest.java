package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void testTasksEqualityById() {
        Task task1 = new Task("Test", "Description");
        Task task2 = new Task("Different title", "Different description");

        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Tasks with same id should be equal");
        assertEquals(task1.hashCode(), task2.hashCode(), "Hash codes should be equal");
    }

    @Test
    void testDifferentTasksWithDifferentIds() {
        Task task1 = new Task("Test", "Description");
        Task task2 = new Task("Test", "Description");

        task1.setId(1);
        task2.setId(2);

        assertNotEquals(task1, task2, "Tasks with different ids should not be equal");
    }

    @Test
    void testInheritedTasksEqualityById() {
        Task task = new Task("Task", "Task Description");
        Epic epic = new Epic("Epic", "Epic Description");
        Subtask subtask = new Subtask("Subtask", "Subtask Description", 10);

        task.setId(5);
        epic.setId(5);
        subtask.setId(5);

        assertEquals(task.hashCode(), epic.hashCode(), "Task and Epic with same id should have equal hash codes");
        assertEquals(task.hashCode(), subtask.hashCode(), "Task and Subtask with same id should have equal hash codes");

        assertEquals(task, epic, "Task and Epic with same id should be equal");
        assertEquals(task, subtask, "Task and Subtask with same id should be equal");
        assertEquals(epic, subtask, "Epic and Subtask with same id should be equal");
    }
}