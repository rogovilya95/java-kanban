package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIds;

    public Epic(String title, String description) {
        super(title, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(int id, String title, String description, ArrayList<Integer> subtaskIds) {
        super(id, title, description);
        this.subtaskIds = subtaskIds != null ? new ArrayList<>(subtaskIds) : new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds != null ? new ArrayList<>(subtaskIds) : new ArrayList<>();
    }

    public void addSubtaskId(int id) {
        this.subtaskIds.add(id);
    }

    public void removeSubtaskId(Integer id) {
        this.subtaskIds.remove(id);
    }

    @Override
    public void setStatus(Status status) {
        throw new UnsupportedOperationException("Epic status cannot be set directly. It is calculated based on subtasks.");
    }

    public void updateStatusFromTaskManager(Status status) {
        super.setStatus(status);
    }

    @Override
    public Epic copy() {
        Epic copy = new Epic(this.getTitle(), this.getDescription());
        copy.setId(this.getId());
        copy.updateStatusFromTaskManager(this.getStatus());
        copy.setSubtaskIds(new ArrayList<>(this.getSubtaskIds()));
        return copy;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", subtaskIds=" + subtaskIds +
                '}';
    }
}