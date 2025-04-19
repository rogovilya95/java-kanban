package repository;

import model.Subtask;

import java.util.List;

public interface SubtaskRepository {
    Subtask findSubtaskById(int id);
    Subtask findSubtaskByName(String name);
    Subtask findSubtaskByDescription(String description);
    List<Subtask> findAllSubtasks();
    List<Subtask> findByEpicId(int epicId);
    void saveSubtask(Subtask subtask);
    void deleteSubtask(int id);
    void deleteAllSubtasks();

}
