package service;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.List;

public class TaskManager implements TaskService,  SubtaskService, EpicService {
    @Override
    public void createEpic(Epic epic) {

    }

    @Override
    public List<Epic> getAllEpics() {
        return List.of();
    }

    @Override
    public Epic getEpic(int id) {
        return null;
    }

    @Override
    public void updateEpic(Epic epic) {

    }

    @Override
    public void updateEpicStatus(int id) {

    }

    @Override
    public void deleteEpic(int id) {

    }

    @Override
    public void deleteAllEpics() {

    }

    @Override
    public void createSubtask(Subtask subtask) {

    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return List.of();
    }

    @Override
    public Subtask getsubtask(int id) {
        return null;
    }

    @Override
    public void updateSubtask(Subtask subtask) {

    }

    @Override
    public void updateSubtaskStatus(int id) {

    }

    @Override
    public void deleteSubtask(int id) {

    }

    @Override
    public void deleteAllSubtask() {

    }

    @Override
    public void createTask(Task task) {

    }

    @Override
    public List<Task> getAllTasks() {
        return List.of();
    }

    @Override
    public Task getTask(int id) {
        return null;
    }

    @Override
    public void updateTask(Task task) {

    }

    @Override
    public void updateTaskStatus(int id) {

    }

    @Override
    public void deleteTask(int id) {

    }

    @Override
    public void deleteAllTasks() {

    }
}
