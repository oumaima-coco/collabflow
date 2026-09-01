package com.collabflow.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskEventListener {
    private final NotificationRepository notificationRepository;

    public TaskEventListener(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(topics = "task-assigned", groupId = "notification-service")
    public void handleTaskAssigned(TaskAssignedEvent event) {
        String message = "You were assigned to task: " + event.getTaskTitle();
        Notification notification = new Notification(event.getAssigneeId(), message);
        notificationRepository.save(notification);
    }
}
