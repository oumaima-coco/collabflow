package com.collabflow.activityservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskEventListener {
    private final ActivityLogRepository activityLogRepository;

    public TaskEventListener(ActivityLogRepository activityLogRepository) {
        this.activityLogRepository = activityLogRepository;
    }

    @KafkaListener(topics = "task-created", groupId = "activity-service", containerFactory = "taskCreatedListenerFactory")
    public void handleTaskCreated(TaskCreatedEvent event) {
        String description = "Task \"" + event.getTaskTitle() + "\" was created";
        ActivityLog log = new ActivityLog(event.getTaskId(), event.getProjectId(), description);
        activityLogRepository.save(log);
    }

    @KafkaListener(topics = "task-assigned", groupId = "activity-service", containerFactory = "taskAssignedListenerFactory")
    public void handleTaskAssigned(TaskAssignedEvent event) {
        String description = "Task \"" + event.getTaskTitle() + "\" was assigned to user #" + event.getAssigneeId();
        ActivityLog log = new ActivityLog(event.getTaskId(), event.getProjectId(), description);
        activityLogRepository.save(log);
    }

    @KafkaListener(topics = "task-status-changed", groupId = "activity-service", containerFactory = "taskStatusChangedListenerFactory")
    public void handleTaskStatusChanged(TaskStatusChangedEvent event) {
        String description = "Task \"" + event.getTaskTitle() + "\" moved from " + event.getOldStatus() + " to " + event.getNewStatus();
        ActivityLog log = new ActivityLog(event.getTaskId(), event.getProjectId(), description);
        activityLogRepository.save(log);
    }
}
