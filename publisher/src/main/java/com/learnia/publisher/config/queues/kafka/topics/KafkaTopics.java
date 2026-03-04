package com.learnia.publisher.config.queues.kafka.topics;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopics{
    
    @Bean
    public NewTopic knowledgementTopic() {
        return new NewTopic("knowledgement-topic", 2, (short) 1);
    }
}
