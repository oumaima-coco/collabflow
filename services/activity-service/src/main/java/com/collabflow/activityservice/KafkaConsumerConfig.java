package com.collabflow.activityservice;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "activity-service");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        return config;
    }

    @Bean
    public ConsumerFactory<String, TaskCreatedEvent> taskCreatedConsumerFactory() {
        Map<String, Object> config = baseConfig();
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(TaskCreatedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskCreatedEvent> taskCreatedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskCreatedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskCreatedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TaskAssignedEvent> taskAssignedConsumerFactory() {
        Map<String, Object> config = baseConfig();
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(TaskAssignedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskAssignedEvent> taskAssignedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskAssignedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskAssignedConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, TaskStatusChangedEvent> taskStatusChangedConsumerFactory() {
        Map<String, Object> config = baseConfig();
        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(TaskStatusChangedEvent.class, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TaskStatusChangedEvent> taskStatusChangedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, TaskStatusChangedEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(taskStatusChangedConsumerFactory());
        return factory;
    }
}