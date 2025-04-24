package repository;

import model.Epic;

import java.util.ArrayList;
import java.util.List;

public interface EpicRepository {
    Epic findEpicById(int id);
    ArrayList<Epic> findEpicByTitle(String title);
    ArrayList<Epic> findEpicByDescription(String description);
    List<Epic> findAllEpics();
    Epic saveEpic(Epic epic);
    void updateEpic(Epic epic);
    void deleteEpic(int id);
    void deleteAllEpics();
}
