package service;

import model.Epic;

import java.util.List;

public interface EpicService {
    void createEpic(Epic epic);
    List<Epic> getAllEpics();
    Epic getEpic(int id);
    void updateEpic(Epic epic);
    void updateEpicStatus(int id);
    void deleteEpic(int id);
    void deleteAllEpics();
}
