package com.example.user_service.services;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQService {

    @Autowired
    private UserService userService;

    @RabbitListener(queues = "${spring.rabbitmq.queue.user}")
    public void receiveMessage(String userId) {
        userId = userId.replace("\"", "");
        userService.createUser(userId);
        System.out.println("Created User with ID: " + userId);
    }
}
