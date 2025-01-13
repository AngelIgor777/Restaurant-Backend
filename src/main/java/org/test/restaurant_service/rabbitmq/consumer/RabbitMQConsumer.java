package org.test.restaurant_service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.service.impl.TelegramUserServiceImpl;

import com.rabbitmq.client.Channel;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final TelegramUserServiceImpl otpService;

    /**
     * Метод для обработки сообщений из RabbitMQ.
     *
     * @param message сообщение, полученное из очереди
     * @param channel канал RabbitMQ для управления подтверждениями
     */
    @RabbitListener(queues = "#{rabbitMQConfig.getJsonQueueName()}", ackMode = "MANUAL") // Ручное подтверждение
    public void consumeJsonMessage(Message message, Channel channel) {
        try {
            // Десериализация сообщения
            UserRegistrationDTO user = deserializeMessage(message);

            log.info("Consumed User message: {}", user);

            // Обработка сообщения
            otpService.save(user);

            // Подтверждаем успешную обработку
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            log.error("Error processing message: {}", e.getMessage(), e);

            try {
                // Отклоняем сообщение и удаляем его из очереди
                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
            } catch (Exception ex) {
                log.error("Failed to reject message: {}", ex.getMessage(), ex);
            }
        }
    }

    /**
     * Метод для десериализации сообщения в UserRegistrationDTO.
     *
     * @param message сообщение RabbitMQ
     * @return объект UserRegistrationDTO
     */
    private UserRegistrationDTO deserializeMessage(Message message) {
        // Логика десериализации сообщения из JSON в объект UserRegistrationDTO
        // Например, используя ObjectMapper из Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getBody(), UserRegistrationDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }
}
