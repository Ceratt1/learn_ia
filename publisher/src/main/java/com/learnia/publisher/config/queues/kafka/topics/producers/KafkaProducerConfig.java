package com.learnia.publisher.config.queues.kafka.topics.producers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import com.learnia.publisher.models.User;

import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.databind.ser.jdk.StringSerializer;



@Configuration
public class KafkaProducerConfig {
    
    private final String BOOTSTRAP_SERVERS;

    public KafkaProducerConfig(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        this.BOOTSTRAP_SERVERS = bootstrapServers;
    }

    @Bean
    public ProducerFactory<UUID, User> userFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerialize.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<UUID, User> kafkaTemplate() {
        return new org.springframework.kafka.core.KafkaTemplate<>(userFactory());
    }


}
