package com.learnia.producer.service;

import com.learnia.producer.models.User;

public interface IProducerService {
    
    User sendToTopic(User user);

}
