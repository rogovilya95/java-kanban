package repository;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskRepository {
    Task findTaskById(int id);
    ArrayList<Task> findTaskByTitle(String name);
    ArrayList<Task> findTaskByDescription(String description);
    List<Task> findAllTasks();
    Task saveTask(Task task);
    void updateTask(Task task);
    void deleteTask(int id);
    void deleteAllTasks();
}
