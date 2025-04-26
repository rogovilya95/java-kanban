package model;

public enum Status {
    NEW("New"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String status;

    Status(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return status;
    }
}