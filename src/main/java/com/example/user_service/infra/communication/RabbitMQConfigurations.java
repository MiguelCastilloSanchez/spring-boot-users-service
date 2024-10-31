package com.example.user_service.infra.communication;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfigurations {
    
    @Value("${spring.rabbitmq.exchange.app}")
    private String EXCHANGE;

    @Value("${spring.rabbitmq.queue.user}")
    private String USER_QUEUE;

    @Value("${spring.rabbitmq.queue.auth}")
    private String AUTH_QUEUE;

    @Value("${spring.rabbitmq.queue.posts}")
    private String POSTS_QUEUE;

    @Value("${spring.rabbitmq.routing.key.user}")
    private String ROUTING_KEY;

    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    @Bean
    public Queue authQueue() {
        return new Queue(AUTH_QUEUE, true);
    }

    @Bean
    public Queue postsQueue() {
        return new Queue(POSTS_QUEUE, true);
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Binding bindingAuthQueue(Queue authQueue, DirectExchange exchange) {
        return BindingBuilder.bind(authQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public Binding bindingPostsQueue(Queue postsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(postsQueue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}
