package repository;

import exception.TaskNotFoundException;
import model.Epic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EpicRepositoryImpl implements EpicRepository {

    private final Map<Integer, Epic> epics = new HashMap<>();

    @Override
    public Epic findEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public ArrayList<Epic> findEpicByTitle(String title) {
        ArrayList<Epic> result = new ArrayList<>();
        if (title == null) {
            return result;
        }
        for (Epic epic : epics.values()) {
            if(title.equalsIgnoreCase(epic.getTitle())) {
                result.add(epic);
            }
        }
        return result;
    }

    @Override
    public ArrayList<Epic> findEpicByDescription(String description) {
        ArrayList<Epic> result = new ArrayList<>();
        if (description == null) {
            return result;
        }
        for (Epic epic : epics.values()) {
            if(description.equalsIgnoreCase(epic.getDescription())) {
                result.add(epic);
            }
        }
        return result;
    }

    @Override
    public List<Epic> findAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public Epic saveEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        if(!epics.containsKey(epic.getId())) {
            throw new TaskNotFoundException(epic.getId());
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpic(int id) {
        epics.remove(id);
        // Note: TaskManager will handle deleting the subtasks, not this class.
        // This is because connecting tasks is business logic, not storage.
    }

    @Override
    public void deleteAllEpics() {
        epics.clear();
        // Note: TaskManager will delete the subtasks too.
        // Repositories should only work with their own data.
    }
}