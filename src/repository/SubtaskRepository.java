package repository;

import model.Subtask;

import java.util.ArrayList;
import java.util.List;

public interface SubtaskRepository {

    Subtask findSubtaskById(int id);

    ArrayList<Subtask> findSubtaskByTitle(String name);

    ArrayList<Subtask> findSubtaskByDescription(String description);

    List<Subtask> findAllSubtasks();

    List<Subtask> findByEpicId(int epicId);

    Subtask saveSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtask(int id);

    void deleteAllSubtasks();

}
