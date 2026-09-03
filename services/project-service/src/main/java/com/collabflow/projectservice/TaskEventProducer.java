package com.collabflow.projectservice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {
    private static final String TASK_ASSIGNED_TOPIC = "task-assigned";
    private static final String TASK_CREATED_TOPIC = "task-created";
    private static final String TASK_STATUS_CHANGED_TOPIC = "task-status-changed";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskAssigned(TaskAssignedEvent event) {
        kafkaTemplate.send(TASK_ASSIGNED_TOPIC, event.getTaskId().toString(), event);
    }

    public void publishTaskCreated(TaskCreatedEvent event) {
        kafkaTemplate.send(TASK_CREATED_TOPIC, event.getTaskId().toString(), event);
    }

    public void publishTaskStatusChanged(TaskStatusChangedEvent event) {
        kafkaTemplate.send(TASK_STATUS_CHANGED_TOPIC, event.getTaskId().toString(), event);
    }
}
