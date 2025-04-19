package repository;

import model.Task;

import java.util.List;

public class EpicRepositoryImpl implements  EpicRepository {
    @Override
    public Task findEpicById(int id) {
        return null;
    }

    @Override
    public Task findEpicByName(String name) {
        return null;
    }

    @Override
    public Task findEpicByDescription(String description) {
        return null;
    }

    @Override
    public List<Task> findAllEpics() {
        return List.of();
    }

    @Override
    public void saveEpic(Task task) {

    }

    @Override
    public void deleteEpic(int id) {

    }

    @Override
    public void deleteAllEpics() {

    }
}
