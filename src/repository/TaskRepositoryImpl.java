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
        Task task = tasks.get(id);
        if (task == null) {
            return null;
        }
        return copyTask(task);
    }

    @Override
    public ArrayList<Task> findTaskByTitle(String title) {
        ArrayList<Task> result = new ArrayList<>();

        if (title == null) {
            return result;
        }

        for (Task task : tasks.values()) {
            if (title.equalsIgnoreCase(task.getTitle())) {
                result.add(copyTask(task));
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
            if (description.equalsIgnoreCase(task.getDescription())) {
                result.add(copyTask(task));
            }
        }

        return result;
    }

    @Override
    public List<Task> findAllTasks() {
        List<Task> result = new ArrayList<>();
        for (Task task : tasks.values()) {
            result.add(copyTask(task));
        }
        return result;
    }

    @Override
    public Task saveTask(Task task) {
        Task taskCopy = copyTask(task);
        tasks.put(taskCopy.getId(), taskCopy);
        return copyTask(taskCopy);
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            throw new TaskNotFoundException(task.getId());
        }
        tasks.put(task.getId(), copyTask(task));
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    private Task copyTask(Task original) {
        Task copy = new Task(original.getTitle(), original.getDescription());
        copy.setId(original.getId());
        copy.setStatus(original.getStatus());
        return copy;
    }
}