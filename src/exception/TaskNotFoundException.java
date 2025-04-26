package exception;

/**
 * We use RuntimeException here because:
 * 1. A missing task usually means there's a bug in our code
 * 2. We don't want to force every method to handle this error
 * 3. When a task is not found, there's usually nothing useful we can do about it
 */
public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException(int id) {
        super("Task with Id " + id + " not found");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }

}
