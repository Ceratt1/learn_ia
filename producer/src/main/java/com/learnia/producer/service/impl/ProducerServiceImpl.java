package com.learnia.producer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.learnia.producer.models.User;
import com.learnia.producer.models.dto.UserEventDto;
import com.learnia.producer.service.IProducerService;

import jakarta.annotation.Nullable;

@Service
public class ProducerServiceImpl implements IProducerService {

    private final KafkaTemplate<String, UserEventDto> kafkaTemplate;

    private final String TOPIC = "knowledgement-topic";

    @Autowired
    public ProducerServiceImpl(KafkaTemplate<String, UserEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }



    @Override
    public User sendToTopic(@Nullable User user) {
        kafkaTemplate.send(TOPIC, user.getUuid().toString(), UserEventDto.from(user));
        return user;
    }
    
}
