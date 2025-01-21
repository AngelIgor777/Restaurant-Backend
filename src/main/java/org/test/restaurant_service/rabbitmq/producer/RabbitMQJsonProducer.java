package org.test.restaurant_service.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQJsonProducer {

    @Value("${rabbitmq.exchanges.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queues.json.queue1.routingKey}")
    private String jsonUserRegistrationRoutingKey;

    @Value("${rabbitmq.queues.json.queue2.name}")
    private String jsonProductSaveQueue;

    @Value("${rabbitmq.queues.json.queue2.routingKey}")
    private String jsonProductSaveRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public void send(UserRegistrationDTO jsonUserRegistrationDTO) {
        log.info("Отправка сообщения в RabbitMQ: {}", jsonUserRegistrationDTO);
        rabbitTemplate.convertAndSend(exchangeName, jsonUserRegistrationRoutingKey, jsonUserRegistrationDTO);
    }

    public void send(ProductRequestDTO productRequestDTO) {
        log.info("Отправка сообщения в RabbitMQ: {}", productRequestDTO);
        rabbitTemplate.convertAndSend(exchangeName, jsonProductSaveQueue, jsonProductSaveRoutingKey);
    }

}
