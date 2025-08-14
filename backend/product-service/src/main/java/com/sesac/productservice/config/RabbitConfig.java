package com.sesac.productservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitConfig {

    @Value("${order.event.exchange}")
    private String exchange;

    @Value("${order.event.queue.notification}")
    private String notificationQueue;

    @Value("${order.event.queue.payment-request}")
    private String paymentRequestQueue;
    @Value("${order.event.queue.inventory-failed}")
    private String inventoryFailedQueue;

    @Value("${order.event.queue.inventory}")
    private String inventoryQueue;

    @Value("${order.event.routing-key.notification}")
    private String notificationRoutingKey;

    @Value("${order.event.routing-key.inventory}")
    private String inventoryRoutingKey;

    @Value("${order.event.routing-key.payment-request}")
    private String paymentRoutingKey;
    @Value("${order.event.routing-key.inventory-failed}")
    private String InventoryRoutingKey;

    // Exchange 정의
    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange(exchange);
    }

    // Queue 정의
    @Bean
    public Queue notificationQueue() {
        return QueueBuilder.durable(notificationQueue).build();
    }
    
    // 사용하진 않지만 공통된 설정해준 것!
    @Bean
    public Queue inventoryQueue() {
        return QueueBuilder.durable(inventoryQueue).build();
    }

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(paymentRequestQueue).build();
    }

    @Bean
    public Queue inventoryFailedQueue() {
        return QueueBuilder.durable(inventoryFailedQueue).build();
    }


    // Binding 정의
    @Bean
    public Binding notificationBinding() {
        return BindingBuilder.bind(notificationQueue())
                             .to(orderExchange())
                             .with(notificationRoutingKey);
    }

    @Bean
    public Binding inventoryBinding() {
        return BindingBuilder.bind(inventoryQueue())
                             .to(orderExchange())
                             .with(inventoryRoutingKey);
    }

    @Bean
    public Binding paymentRequestBinding() {
        return BindingBuilder.bind(paymentRequestQueue())
                .to(orderExchange())
                .with(paymentRoutingKey);
    }

    @Bean
    public Binding InventoryFailedBinding() {
        return BindingBuilder.bind((inventoryFailedQueue()))
                             .to(orderExchange())
                             .with(InventoryRoutingKey);
    }


    // JSON 메시지 컨버터 추가
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }




}
