package service;

import model.Subtask;

import java.util.List;

public interface SubtaskService {
    Subtask createSubtask(Subtask subtask);
    List<Subtask> getAllSubtasks();
    Subtask getSubtask(int id);
    List<Subtask> getEpicSubtasks(int epicId);
    void updateSubtask(Subtask subtask);
    void deleteSubtask(int id);
    void deleteAllSubtask();
}
