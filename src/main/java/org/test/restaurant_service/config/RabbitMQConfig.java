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
    private String jsonUserRegistrationQueue;

    @Value("${rabbitmq.queues.json.queue1.routingKey}")
    private String jsonUserRegistrationRoutingKey;

    @Value("${rabbitmq.queues.json.queue2.name}")
    private String jsonProductSaveQueue;

    @Value("${rabbitmq.queues.json.queue2.routingKey}")
    private String jsonProductSaveRoutingKey;

    @Bean
    public Queue userRegisterQueue() {
        return new Queue(jsonUserRegistrationQueue);
    }

    @Bean
    public Queue productSaveQueue() {
        return new Queue(jsonProductSaveQueue);
    }



    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(exchangeName);
    }


    @Bean
    public Binding userRegisterBinding() {
        return BindingBuilder
                .bind(userRegisterQueue())
                .to(topicExchange())
                .with(jsonUserRegistrationRoutingKey);
    }

    @Bean
    public Binding productSaveBinding() {
        return BindingBuilder
                .bind(productSaveQueue())
                .to(topicExchange())
                .with(jsonProductSaveRoutingKey);
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
