package com.collabflow.projectservice;

public class TaskCreatedEvent {
    private Long taskId;
    private String taskTitle;
    private Long projectId;

    public TaskCreatedEvent() {
    }

    public TaskCreatedEvent(Long taskId, String taskTitle, Long projectId) {
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.projectId = projectId;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
