package com.example.user_service.services;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class RabbitMQService {

    private final AmqpTemplate amqpTemplate;

    @Value("${spring.rabbitmq.exchange.app}")
    private String EXCHANGE;

    @Value("${spring.rabbitmq.routing.key.user}")
    private String ROUTING_KEY;

    @Autowired
    private UserService userService;

    public void sendMessage(String userId) {
        amqpTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, userId);
    }

    @RabbitListener(queues = "${spring.rabbitmq.queue.user}")
    public void receiveMessage(String userId) {
        userId = userId.replace("\"", "");
        userService.createUser(userId);
        System.out.println("Created User with ID: " + userId);
    }

}
