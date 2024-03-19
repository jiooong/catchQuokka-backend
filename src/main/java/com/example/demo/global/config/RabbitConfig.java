package com.example.demo.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

    @Configuration
    @EnableRabbit
    public class RabbitConfig {
        // 사전작업에서 정한 exchange, queue, routing 등의 정보를 토대로 bean 생성
        private static final String EXCHANGE_NAME = "game.exchange";
        private static final String QUEUE_NAME = "game.queue";
        private static final String ROUTING_KEY = "*.room.*";

        @Bean
    TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);    }
    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME);    } // durable 옵션을 주지 않으면

    //Exchange와 queue 바인딩
    @Bean
    Binding binding (Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);    }

    //Rabbitmq와의 메시지 통신을 담당
    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

    // RabbitMQ와의 연결을 관리하는 클래스
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }

}
