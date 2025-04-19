package service;

import model.Epic;
import model.Subtask;

import java.util.List;

public interface SubtaskService {
    void createSubtask(Subtask subtask);
    List<Subtask> getAllSubtasks();
    Subtask getsubtask(int id);
    void updateSubtask(Subtask subtask);
    void updateSubtaskStatus(int id);
    void deleteSubtask(int id);
    void deleteAllSubtask();
}
