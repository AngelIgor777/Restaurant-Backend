package org.test.restaurant_service.config;

import lombok.Getter;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class RabbitMQConfig {

    @Value("${rabbitmq.exchanges.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queues.json.queue1.name}")
    private String orderSavingQueue;

    @Value("${rabbitmq.queues.json.queue1.routingKey}")
    private String orderSavingRoutingKey;


    @Value("${rabbitmq.queues.json.queue2.name}")
    private String productHistorySavingQueue;

    @Value("${rabbitmq.queues.json.queue2.routingKey}")
    private String productHistoryRoutingKey;

    @Bean
    public Queue orderSavingQueue() {
        return new Queue(orderSavingQueue);
    }

    @Bean
    public Queue productHistorySavingQueue() {
        return new Queue(productHistorySavingQueue);
    }



    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }


    @Bean
    public Binding orderSavingBinding() {
        return BindingBuilder
                .bind(orderSavingQueue())
                .to(topicExchange())
                .with(orderSavingRoutingKey);
    }

    @Bean
    public Binding productHistorySaving() {
        return BindingBuilder
                .bind(productHistorySavingQueue())
                .to(topicExchange())
                .with(productHistoryRoutingKey);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter());
        return rabbitTemplate;

    }

}
