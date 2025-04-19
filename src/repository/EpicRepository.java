package repository;

import model.Task;

import java.util.List;

public interface EpicRepository {
    Task findEpicById(int id);
    Task findEpicByName(String name);
    Task findEpicByDescription(String description);
    List<Task> findAllEpics();
    void saveEpic(Task task);
    void deleteEpic(int id);
    void deleteAllEpics();
}
