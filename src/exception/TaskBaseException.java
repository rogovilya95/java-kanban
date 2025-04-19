package exception;

public class TaskBaseException extends RuntimeException{
    public TaskBaseException(String message){
        super(message);
    }

    public TaskBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
