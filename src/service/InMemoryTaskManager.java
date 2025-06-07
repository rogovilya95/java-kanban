package service;

import exception.TaskNotFoundException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import repository.EpicRepository;
import repository.SubtaskRepository;
import repository.TaskRepository;

import java.util.List;

public class InMemoryTaskManager implements TaskManager{

    private final IdGenerator idGenerator;
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;
    private final EpicRepository epicRepository;
    private final HistoryManager historyManager;

    public InMemoryTaskManager(IdGenerator idGenerator,
                               TaskRepository taskRepository,
                               SubtaskRepository subtaskRepository,
                               EpicRepository epicRepository,
                               HistoryManager historyManager) {
        this.idGenerator = idGenerator;
        this.taskRepository = taskRepository;
        this.subtaskRepository = subtaskRepository;
        this.epicRepository = epicRepository;
        this.historyManager = historyManager;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic epicCopy = new Epic(epic.getTitle(), epic.getDescription());
        epicCopy.setId(idGenerator.generateId());
        Epic createdEpic = epicRepository.saveEpic(epicCopy);
        updateEpicStatus(createdEpic.getId());
        return createdEpic;
    }

    @Override
    public List<Epic> getAllEpics() {
        return epicRepository.findAllEpics();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epicRepository.findEpicById(id);
        if (epic != null) {
            Epic epicCopy = copyEpic(epic);
            historyManager.add(epicCopy);
            return epicCopy;
        }
        return null;
    }

    @Override
    public void updateEpic(Epic epic) {
        epicRepository.updateEpic(epic);
        updateEpicStatus(epic.getId());
    }

    private void updateEpicStatus(int id) {
        Epic epic = epicRepository.findEpicById(id);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + id +" not found");
        }
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());

        Status newStatus;
        if (subtasks.isEmpty()) {
            newStatus = Status.NEW;
        } else {
            boolean allNew = true;
            boolean allDone = true;
            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() != Status.NEW) {
                    allNew = false;
                }
                if (subtask.getStatus() != Status.DONE) {
                    allDone = false;
                }
            }

            if (allNew) {
                newStatus = Status.NEW;
            } else if (allDone) {
                newStatus = Status.DONE;
            } else {
                newStatus = Status.IN_PROGRESS;
            }
        }

        epic.updateStatusFromTaskManager(newStatus);
        epicRepository.updateEpic(epic);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epicToDelete = epicRepository.findEpicById(id);
        if (epicToDelete == null) {
            throw new TaskNotFoundException("Epic with id " + id + " not found");
        }

        for (Integer subtaskId : epicToDelete.getSubtaskIds()) {
            subtaskRepository.deleteSubtask(subtaskId);
            historyManager.remove(subtaskId);
        }

        epicRepository.deleteEpic(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : getAllEpics()) {
            historyManager.remove(epic.getId());
        }
        for (Subtask subtask : getAllSubtasks()) {
            historyManager.remove(subtask.getId());
        }

        subtaskRepository.deleteAllSubtasks();
        epicRepository.deleteAllEpics();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask.getId() != 0 && subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Subtask cannot be its own epic");
        }

        Epic epic = epicRepository.findEpicById(subtask.getEpicId());
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + subtask.getEpicId() + " not found");
        }

        Subtask subtaskCopy = new Subtask(subtask.getTitle(), subtask.getDescription(), subtask.getEpicId());
        if (subtask.getStatus() != null) {
            subtaskCopy.setStatus(subtask.getStatus());
        }
        subtaskCopy.setId(idGenerator.generateId());

        Subtask createdSubtask = subtaskRepository.saveSubtask(subtaskCopy);
        epic.addSubtaskId(subtaskCopy.getId());
        epicRepository.updateEpic(epic);
        updateEpicStatus(epic.getId());

        return createdSubtask;
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return subtaskRepository.findAllSubtasks();
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtaskRepository.findSubtaskById(id);
        if (subtask != null) {
            Subtask subtaskCopy = copySubtask(subtask);
            historyManager.add(subtaskCopy);
            return subtaskCopy;
        }
        return null;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epicRepository.findEpicById(epicId);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + epicId + " not found");
        }
        return subtaskRepository.findByEpicId(epicId);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask.getId() == subtask.getEpicId()) {
            throw new IllegalArgumentException("Subtask cannot be its own epic");
        }

        Subtask subtaskToUpdate = subtaskRepository.findSubtaskById(subtask.getId());
        if (subtaskToUpdate == null) {
            throw new TaskNotFoundException("Subtask with id " + subtask.getId() + " not found");
        }

        int oldEpicId = subtaskToUpdate.getEpicId();
        int newEpicId = subtask.getEpicId();

        if (oldEpicId != newEpicId) {
            Epic oldEpic = epicRepository.findEpicById(oldEpicId);
            Epic newEpic = epicRepository.findEpicById(newEpicId);
            if (newEpic == null) {
                throw new TaskNotFoundException("Epic with id " + newEpicId + " not found");
            }
            if (oldEpic != null) {
                oldEpic.removeSubtaskId(subtask.getId());
                epicRepository.updateEpic(oldEpic);
                updateEpicStatus(oldEpic.getId());
            }
            newEpic.addSubtaskId(subtask.getId());
            epicRepository.updateEpic(newEpic);
        }
        subtaskRepository.updateSubtask(subtask);
        updateEpicStatus(subtask.getEpicId());
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtaskToDelete = subtaskRepository.findSubtaskById(id);
        if (subtaskToDelete == null) {
            throw new TaskNotFoundException("Subtask with id " + id + " not found");
        }
        Epic epic = epicRepository.findEpicById(subtaskToDelete.getEpicId());
        if (epic != null) {
            epic.removeSubtaskId(id);
            epicRepository.updateEpic(epic);
            updateEpicStatus(epic.getId());
        }
        subtaskRepository.deleteSubtask(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllSubtask() {
        for (Subtask subtask : getAllSubtasks()) {
            historyManager.remove(subtask.getId());
        }

        List<Epic> allEpics = epicRepository.findAllEpics();

        for (Epic epic : allEpics) {
            Epic currentEpic = epicRepository.findEpicById(epic.getId());
            if (currentEpic != null) {
                currentEpic.setSubtaskIds(new java.util.ArrayList<>());
                currentEpic.updateStatusFromTaskManager(Status.NEW);
                epicRepository.updateEpic(currentEpic);
            }
        }

        subtaskRepository.deleteAllSubtasks();
    }

    @Override
    public Task createTask(Task task) {
        Task taskCopy = new Task(task.getTitle(), task.getDescription());
        if (task.getStatus() != null) {
            taskCopy.setStatus(task.getStatus());
        } else {
            taskCopy.setStatus(Status.NEW);
        }
        taskCopy.setId(idGenerator.generateId());

        return taskRepository.saveTask(taskCopy);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAllTasks();
    }

    @Override
    public Task getTask(int id) {
        Task task = taskRepository.findTaskById(id);
        if (task != null) {
            Task taskCopy = copyTask(task);
            historyManager.add(taskCopy);
            return taskCopy;
        }
        return null;
    }

    @Override
    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    @Override
    public void deleteTask(int id) {
        taskRepository.deleteTask(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteAllTasks() {
        for (Task task : getAllTasks()) {
            historyManager.remove(task.getId());
        }

        taskRepository.deleteAllTasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private Task copyTask(Task original) {
        Task copy = new Task(original.getTitle(), original.getDescription());
        copy.setId(original.getId());
        copy.setStatus(original.getStatus());
        return copy;
    }

    private Epic copyEpic(Epic original) {
        Epic copy = new Epic(original.getTitle(), original.getDescription());
        copy.setId(original.getId());
        copy.updateStatusFromTaskManager(original.getStatus());
        copy.setSubtaskIds(new java.util.ArrayList<>(original.getSubtaskIds()));
        return copy;
    }

    private Subtask copySubtask(Subtask original) {
        Subtask copy = new Subtask(original.getTitle(), original.getDescription(), original.getEpicId());
        copy.setId(original.getId());
        copy.setStatus(original.getStatus());
        return copy;
    }
}