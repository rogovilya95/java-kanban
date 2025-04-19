package service;

import model.Task;

import java.util.List;

public interface TaskService {
    void createTask(Task task);
    List<Task> getAllTasks();
    Task getTask(int id);
    void updateTask(Task task);
    void updateTaskStatus(int id);
    void deleteTask(int id);
    void deleteAllTasks();
}
