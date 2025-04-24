import service.IdGenerator;
import service.IdGeneratorImpl;
import service.TaskManager;
import repository.*;
import model.*;
import exception.TaskNotFoundException;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        try {
            IdGenerator idGenerator = new IdGeneratorImpl();
            TaskRepository taskRepository = new TaskRepositoryImpl();
            EpicRepository epicRepository = new EpicRepositoryImpl();
            SubtaskRepository subtaskRepository = new SubtaskRepositoryImpl();
            TaskManager taskManager = new TaskManager(idGenerator, taskRepository, subtaskRepository, epicRepository);

            // 1. Создаем две задачи
            System.out.println("##############################");
            System.out.println("### Создание обычных задач ###");
            System.out.println("##############################");
            Task task1 = taskManager.createTask(new Task("Доделать проект", "до 21го числа"));
            Task task2 = taskManager.createTask(new Task("Заплатить за страховку", "Пенсия, здоровье"));

            System.out.println("Созданы задачи:");
            for (Task task : taskManager.getAllTasks()) {
                System.out.println("- " + task);
            }

            // 2. Создаем эпик с двумя подзадачами
            System.out.println("\n####################################");
            System.out.println("### Создание эпика с подзадачами ###");
            System.out.println("####################################");
            Epic birthday = new Epic("День рождения", "Организационные моменты");
            birthday = taskManager.createEpic(birthday);

            Subtask invites = new Subtask("Приглашения", "Илья, Камила, Ваня, Коля", birthday.getId());
            Subtask groceries = new Subtask("Купить продукты", "Закупиться", birthday.getId());

            invites = taskManager.createSubtask(invites);
            groceries = taskManager.createSubtask(groceries);

            System.out.println("Создан эпик: " + birthday);
            System.out.println("Подзадачи эпика:");
            for (Subtask subtask : taskManager.getEpicSubtasks(birthday.getId())) {
                System.out.println("- " + subtask);
            }

            // 3. Создаем эпик с одной подзадачей
            System.out.println("\n#########################################");
            System.out.println("### Создание эпика с одной подзадачей ###");
            System.out.println("#########################################");
            Epic workTask = new Epic("Пофиксить swagger", "Сделать UI красивее");
            workTask = taskManager.createEpic(workTask);

            Subtask checkEndpoints = new Subtask("Проверить endpoints", "Добавить описания", workTask.getId());
            taskManager.createSubtask(checkEndpoints);

            System.out.println("Создан эпик: " + workTask);
            System.out.println("Подзадачи эпика:");
            for (Subtask subtask : taskManager.getEpicSubtasks(workTask.getId())) {
                System.out.println("- " + subtask);
            }

            // 4. Выводим списки всех задач
            System.out.println("\n#########################");
            System.out.println("### Списки всех задач ###");
            System.out.println("#########################");
            System.out.println("Обычные задачи:");
            for (Task task : taskManager.getAllTasks()) {
                System.out.println("- " + task);
            }

            System.out.println("\nЭпики:");
            for (Epic epic : taskManager.getAllEpics()) {
                System.out.println("- " + epic);
            }

            System.out.println("\nПодзадачи:");
            for (Subtask subtask : taskManager.getAllSubtasks()) {
                System.out.println("- " + subtask);
            }

            // 5. Изменяем статусы задач
            System.out.println("\n################################");
            System.out.println("### Изменение статусов задач ###");
            System.out.println("################################");

            // Обновляем статус задачи
            task1.setStatus(Status.IN_PROGRESS);
            taskManager.updateTask(task1);
            System.out.println("Обновлен статус задачи '" + task1.getTitle() + "': " + task1.getStatus());

            // Обновляем статусы подзадач и проверяем статуса эпика
            invites.setStatus(Status.DONE);
            groceries.setStatus(Status.IN_PROGRESS);

            taskManager.updateSubtask(invites);
            taskManager.updateSubtask(groceries);

            System.out.println("Обновлены статусы подзадач эпика 'День рождения'");
            System.out.println("- " + invites.getTitle() + ": " + invites.getStatus());
            System.out.println("- " + groceries.getTitle() + ": " + groceries.getStatus());
            System.out.println("Статус эпика 'День рождения': " + taskManager.getEpic(birthday.getId()).getStatus());

            // Теперь делаем все подзадачи DONE и проверим статус эпика
            groceries.setStatus(Status.DONE);
            taskManager.updateSubtask(groceries);

            System.out.println("Все подзадачи эпика 'День рождения' выполнены");
            System.out.println("Статус эпика 'День рождения': " + taskManager.getEpic(birthday.getId()).getStatus());

            // 6. Удаление задач
            System.out.println("\n######################");
            System.out.println("### Удаление задач ###");
            System.out.println("######################");


            // Удаляем задачу
            taskManager.deleteTask(task2.getId());
            System.out.println("Задача '" + task2.getTitle() + "' удалена");
            System.out.println("Оставшиеся обычные задачи: " + taskManager.getAllTasks().size());

            // Удаляем эпик вместе с подзадачами
            taskManager.deleteEpic(workTask.getId());
            System.out.println("Эпик '" + workTask.getTitle() + "' удален");
            System.out.println("Оставшиеся эпики: " + taskManager.getAllEpics().size());
            System.out.println("Оставшиеся подзадачи: " + taskManager.getAllSubtasks().size());

            // 7. Поиск задач по имени
            System.out.println("\n###############################");
            System.out.println("### Поиск задач по названию ###");
            System.out.println("###############################");
            List<Task> foundTasks = taskRepository.findTaskByTitle("Доделать проект");
            System.out.println("Найдено задач с названием 'Доделать проект': " + foundTasks.size());
            for (Task task : foundTasks) {
                System.out.println("- " + task);
            }

            // 8. Выводим финальное состояние системы
            System.out.println("\n###################################");
            System.out.println("### Финальное состояние системы ###");
            System.out.println("###################################");
            System.out.println("Обычные задачи: " + taskManager.getAllTasks().size());
            System.out.println("Эпики: " + taskManager.getAllEpics().size());
            System.out.println("Подзадачи: " + taskManager.getAllSubtasks().size());

            // Выводим информацию об оставшихся задачах
            if (!taskManager.getAllTasks().isEmpty()) {
                System.out.println("Оставшиеся обычные задачи:");
                for (Task task : taskManager.getAllTasks()) {
                    System.out.println("- " + task);
                }
            }

            if (!taskManager.getAllEpics().isEmpty()) {
                System.out.println("Оставшиеся эпики:");
                for (Epic epic : taskManager.getAllEpics()) {
                    System.out.println("- " + epic);

                    List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(epic.getId());
                    System.out.println("Подзадачи (" + epicSubtasks.size() + "):");
                    for (Subtask subtask : epicSubtasks) {
                        System.out.println("* " + subtask);
                    }
                }
            }

        } catch (TaskNotFoundException e) {
            System.err.println("Ошибка при работе с задачами: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Непредвиденная ошибка: " + e.getMessage());
        }
    }
}