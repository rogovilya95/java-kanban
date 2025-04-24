package exception;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(int id) {
        super("Task with Id " + id + " not found");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }

}
