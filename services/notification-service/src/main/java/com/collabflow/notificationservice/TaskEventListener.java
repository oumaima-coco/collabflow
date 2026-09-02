package com.collabflow.notificationservice;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class TaskEventListener {
    private final NotificationRepository notificationRepository;
    private final SseEmitterManager sseEmitterManager;

    public TaskEventListener(NotificationRepository notificationRepository, SseEmitterManager sseEmitterManager) {
        this.notificationRepository = notificationRepository;
        this.sseEmitterManager = sseEmitterManager;
    }

    @KafkaListener(topics = "task-assigned", groupId = "notification-service")
    public void handleTaskAssigned(TaskAssignedEvent event) {
        String message = "You were assigned to task: " + event.getTaskTitle();
        Notification notification = new Notification(event.getAssigneeId(), message);
        Notification savedNotification = notificationRepository.save(notification);

        sseEmitterManager.sendToUser(event.getAssigneeId(), savedNotification);
    }
}