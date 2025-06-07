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
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return null;
        }
        return copySubtask(subtask);
    }

    @Override
    public ArrayList<Subtask> findSubtaskByTitle(String title) {
        ArrayList<Subtask> result = new ArrayList<>();

        if (title == null) {
            return result;
        }

        for(Subtask subtask : subtasks.values()) {
            if (title.equalsIgnoreCase(subtask.getTitle())) {
                result.add(copySubtask(subtask));
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
                result.add(copySubtask(subtask));
            }
        }
        return result;
    }

    @Override
    public List<Subtask> findAllSubtasks() {
        List<Subtask> result = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            result.add(copySubtask(subtask));
        }
        return result;
    }

    @Override
    public List<Subtask> findByEpicId(int epicId) {
        List<Integer> subtaskIds = subtasksByEpicId.getOrDefault(epicId, new ArrayList<>());
        return subtaskIds.stream()
                .map(subtasks::get)
                .map(this::copySubtask)
                .collect(Collectors.toList());
    }

    @Override
    public Subtask saveSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();
        int epicId = subtask.getEpicId();

        Subtask subtaskCopy = copySubtask(subtask);
        subtasks.put(subtaskId, subtaskCopy);

        subtasksByEpicId.computeIfAbsent(epicId, k -> new ArrayList<>()).add(subtaskId);

        return copySubtask(subtaskCopy);
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

        subtasks.put(subtaskId, copySubtask(subtask));
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

    private Subtask copySubtask(Subtask original) {
        Subtask copy = new Subtask(original.getTitle(), original.getDescription(), original.getEpicId());
        copy.setId(original.getId());
        copy.setStatus(original.getStatus());
        return copy;
    }
}