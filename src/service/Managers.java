package service;

import repository.EpicRepositoryImpl;
import repository.SubtaskRepositoryImpl;
import repository.TaskRepositoryImpl;

public class Managers {

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(
                new IdGeneratorImpl(),
                new TaskRepositoryImpl(),
                new SubtaskRepositoryImpl(),
                new EpicRepositoryImpl(),
                getDefaultHistory()
        );
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
