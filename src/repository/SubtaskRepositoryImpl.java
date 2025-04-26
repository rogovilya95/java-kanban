package repository;

import exception.TaskNotFoundException;
import model.Subtask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SubtaskRepositoryImpl implements SubtaskRepository {
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final Map<Integer, List<Integer>> subtasksByEpicId = new HashMap<>();

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
        List<Integer> subtaskIds = subtasksByEpicId.getOrDefault(epicId, new ArrayList<>());
        return subtaskIds.stream()
                .map(subtasks::get)
                .collect(Collectors.toList());
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();

        subtasks.put(subtaskId, subtask);

        subtasksByEpicId.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtaskId);

        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int newEpicId = subtask.getEpicId();

        if (!subtasks.containsKey(subtaskId)) {
            throw new TaskNotFoundException(subtaskId);
        }

        Subtask oldSubtask = subtasks.get(subtaskId);
        int oldEpicId = oldSubtask.getEpicId();

        if (oldEpicId != newEpicId) {
            List<Integer> oldEpicSubtasks = subtasksByEpicId.get(oldEpicId);
            if (oldEpicSubtasks != null) {
                oldEpicSubtasks.remove(Integer.valueOf(subtaskId));
            }

            subtasksByEpicId.computeIfAbsent(newEpicId, k -> new ArrayList<>()).add(subtaskId);
        }

        subtasks.put(subtaskId, subtask);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            subtasks.remove(id);

            List<Integer> epicSubtasks = subtasksByEpicId.get(epicId);
            if (epicSubtasks != null) {
                epicSubtasks.remove(Integer.valueOf(id));
            }
        }
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        subtasksByEpicId.clear();
    }
}