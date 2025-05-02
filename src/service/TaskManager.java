package service;

import exception.TaskNotFoundException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import repository.EpicRepository;
import repository.SubtaskRepository;
import repository.TaskRepository;

import java.util.List;

public interface TaskManager{

    // Task
    Task createTask(Task task);
    List<Task> getAllTasks();
    Task getTask(int id);
    void updateTask(Task task);
    void deleteTask(int id);
    void deleteAllTasks();

    // Subtask
    Subtask createSubtask(Subtask subtask);
    List<Subtask> getAllSubtasks();
    Subtask getSubtask(int id);
    List<Subtask> getEpicSubtasks(int epicId);
    void updateSubtask(Subtask subtask);
    void deleteSubtask(int id);
    void deleteAllSubtask();

    // Epic
    Epic createEpic(Epic epic);
    List<Epic> getAllEpics();
    Epic getEpic(int id);
    void updateEpic(Epic epic);
    void deleteEpic(int id);
    void deleteAllEpics();
    List<Task> getHistory();
}
