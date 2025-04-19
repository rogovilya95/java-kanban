package repository;

import model.Task;

import java.util.List;

public class TaskRepositoryImpl implements TaskRepository {
    @Override
    public Task findTaskById(int id) {
        return null;
    }

    @Override
    public Task findTaskByName(String name) {
        return null;
    }

    @Override
    public Task findTaskByDescription(String description) {
        return null;
    }

    @Override
    public List<Task> findAllTasks() {
        return List.of();
    }

    @Override
    public void saveTask(Task task) {

    }

    @Override
    public void deleteTask(int id) {

    }

    @Override
    public void deleteAllTasks() {

    }
}
