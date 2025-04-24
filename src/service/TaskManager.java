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

public class TaskManager implements TaskService,  SubtaskService, EpicService {

    private final IdGenerator idGenerator;
    private final TaskRepository taskRepository;
    private final SubtaskRepository subtaskRepository;
    private final EpicRepository epicRepository;

    public TaskManager(IdGenerator idGenerator, TaskRepository taskRepository, SubtaskRepository subtaskRepository, EpicRepository epicRepository) {
        this.idGenerator = idGenerator;
        this.taskRepository = taskRepository;
        this.subtaskRepository = subtaskRepository;
        this.epicRepository = epicRepository;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(idGenerator.generateId());
        return epicRepository.saveEpic(epic);

    }

    @Override
    public List<Epic> getAllEpics() {
        return epicRepository.findAllEpics();
    }

    @Override
    public Epic getEpic(int id) {
        return epicRepository.findEpicById(id);
    }

    @Override
    public void updateEpic(Epic epic) {
        epicRepository.updateEpic(epic);
    }

    @Override
    public void updateEpicStatus(int id) {
        Epic epic = epicRepository.findEpicById(id);
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + id +" not found");
        }
        List<Subtask> subtasks = getEpicSubtasks(epic.getId());
        if (subtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }
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
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else  {
            epic.setStatus(Status.IN_PROGRESS);
        }

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
        }
        epicRepository.deleteEpic(id);
    }

    @Override
    public void deleteAllEpics() {
        subtaskRepository.deleteAllSubtasks();
        epicRepository.deleteAllEpics();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {

        Epic epic = epicRepository.findEpicById(subtask.getEpicId());
        if (epic == null) {
            throw new TaskNotFoundException("Epic with id " + subtask.getEpicId() + " not found");
        }

        subtask.setId(idGenerator.generateId());
        Subtask createdSubtask = subtaskRepository.saveSubtask(subtask);
        epic.addSubtaskId(subtask.getId());
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
        return subtaskRepository.findSubtaskById(id);
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
        Subtask subtaskToUpdate = subtaskRepository.findSubtaskById(subtask.getId());
        if (subtaskToUpdate == null) {
            throw new TaskNotFoundException("Subtask with id " + subtask.getId() + " not found");
        }
        if(subtaskToUpdate.getEpicId() != subtask.getEpicId()) {
            Epic oldEpic = epicRepository.findEpicById(subtaskToUpdate.getEpicId());
            Epic newEpic = epicRepository.findEpicById(subtask.getEpicId());
            if (newEpic == null) {
                throw new TaskNotFoundException("Epic with id " + subtask.getEpicId() + " not found");
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
    }

    @Override
    public void deleteAllSubtask() {
        for (Epic epic : epicRepository.findAllEpics()) {
            epic.getSubtaskIds().clear();
            epic.setStatus(Status.NEW);
            epicRepository.updateEpic(epic);
        }
        subtaskRepository.deleteAllSubtasks();
    }

    @Override
    public Task createTask(Task task) {
        task.setId(idGenerator.generateId());
        task.setStatus(Status.NEW);
        return taskRepository.saveTask(task);
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAllTasks();
    }

    @Override
    public Task getTask(int id) {
        return taskRepository.findTaskById(id);
    }

    @Override
    public void updateTask(Task task) {
        taskRepository.updateTask(task);
    }

    @Override
    public void deleteTask(int id) {
        taskRepository.deleteTask(id);
    }

    @Override
    public void deleteAllTasks() {
        taskRepository.deleteAllTasks();
    }
}
