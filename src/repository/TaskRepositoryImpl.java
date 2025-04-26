package repository;

import exception.TaskNotFoundException;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskRepositoryImpl implements TaskRepository {

    private final Map<Integer, Task> tasks = new HashMap<>();

    @Override
    public Task findTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public ArrayList<Task> findTaskByTitle(String title) {
        ArrayList<Task> result = new ArrayList<>();

        if (title == null) {
            return result;
        }

        for (Task task : tasks.values()) {
            if(title.equalsIgnoreCase(task.getTitle())) {
                result.add(task);
            }
        }

        return result;
    }

    @Override
    public ArrayList<Task> findTaskByDescription(String description) {
        ArrayList<Task> result = new ArrayList<>();

        if (description == null) {
            return result;
        }

        for (Task task : tasks.values()) {
            if(description.equalsIgnoreCase(task.getDescription())) {
                result.add(task);
            }
        }

        return result;
    }

    @Override
    public List<Task> findAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public Task saveTask(Task task) {
        // Note: I think it is better to create IDs in TaskManager so all tasks use the same ID system
        // and to keep repositories focused only on storage,
        // also current version is easier to be replaced if i want to change the method of ID generation
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new TaskNotFoundException(task.getId());
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }
}