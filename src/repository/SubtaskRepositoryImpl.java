package repository;

import exception.TaskNotFoundException;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubtaskRepositoryImpl implements  SubtaskRepository {
    Map<Integer, Subtask> subtasks = new HashMap<>();

    @Override
    public Subtask findSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Subtask> findSubtaskByTitle(String title) {
        ArrayList<Subtask> result = new ArrayList<>();

        if (title == null) {
            return result;
        }

        for(Subtask subtask : subtasks.values()) {
            if (title.equalsIgnoreCase(subtask.getTitle())) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Subtask> findSubtaskByDescription(String description) {
        ArrayList<Subtask> result = new ArrayList<>();

        if (description == null) {
            return result;
        }

        for(Subtask subtask : subtasks.values()) {
            if (description.equalsIgnoreCase(subtask.getDescription())) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public List<Subtask> findAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public List<Subtask> findByEpicId(int epicId) {
        ArrayList<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            if (subtask.getEpicId() == epicId) {
                result.add(subtask);
            }
        }
        return result;
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        return subtask;
    }

    public void updateSubtask(Subtask subtask) {
        if (!subtasks.containsKey(subtask.getId())) {
            throw new TaskNotFoundException(subtask.getId());
        }
    }

    @Override
    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
    }
}
