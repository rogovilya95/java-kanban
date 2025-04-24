package service;

import model.Task;

import java.util.List;

public interface TaskService {
    Task createTask(Task task);
    List<Task> getAllTasks();
    Task getTask(int id);
    void updateTask(Task task);
    void deleteTask(int id);
    void deleteAllTasks();
}
