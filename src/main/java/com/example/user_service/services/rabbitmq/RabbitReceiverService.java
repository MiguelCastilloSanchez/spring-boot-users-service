package com.example.user_service.services.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.user_service.entities.users.dtos.RabbitRegisterDTO;
import com.example.user_service.services.UserService;


@Service
@RequiredArgsConstructor
public class RabbitReceiverService {

    @Autowired
    private UserService userService;

    @RabbitListener(queues = "${spring.rabbitmq.queue.user}")
    public void receiveMessage(RabbitRegisterDTO data) {

        userService.createUser(data.userId(), data.name());
        
    }
}
