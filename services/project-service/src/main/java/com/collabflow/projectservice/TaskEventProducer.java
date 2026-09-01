package com.collabflow.projectservice;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {
    private static final String TOPIC = "task-assigned";

    private final KafkaTemplate<String, TaskAssignedEvent> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, TaskAssignedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishTaskAssigned(TaskAssignedEvent event) {
        kafkaTemplate.send(TOPIC, event.getTaskId().toString(), event);
    }
}
