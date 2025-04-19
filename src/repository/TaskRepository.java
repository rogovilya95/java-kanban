package repository;

import model.Task;

import java.util.List;

public interface TaskRepository {
    Task findTaskById(int id);
    Task findTaskByName(String name);
    Task findTaskByDescription(String description);
    List<Task> findAllTasks();
    void saveTask(Task task);
    void deleteTask(int id);
    void deleteAllTasks();
}
