package repository;

import model.Subtask;

import java.util.List;

public class SubtaskRepositoryImpl implements  SubtaskRepository {
    @Override
    public Subtask findSubtaskById(int id) {
        return null;
    }

    @Override
    public Subtask findSubtaskByName(String name) {
        return null;
    }

    @Override
    public Subtask findSubtaskByDescription(String description) {
        return null;
    }

    @Override
    public List<Subtask> findAllSubtasks() {
        return List.of();
    }

    @Override
    public List<Subtask> findByEpicId(int epicId) {
        return List.of();
    }

    @Override
    public void saveSubtask(Subtask subtask) {

    }

    @Override
    public void deleteSubtask(int id) {

    }

    @Override
    public void deleteAllSubtasks() {

    }
}
