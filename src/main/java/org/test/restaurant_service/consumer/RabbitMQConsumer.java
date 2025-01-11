package org.test.restaurant_service.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.service.impl.OtpServiceImpl;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final OtpServiceImpl otpService;

    /**
     * Метод для обработки сообщений из RabbitMQ.
     *
     * @param user сообщение, полученное из очереди
     */
    @RabbitListener(queues = "#{rabbitMQConfig.getJsonQueueName()}")  // Используйте ваше имя очереди
    public void consumeJsonMessage(UserRegistrationDTO user) {
        log.info("Consumed User message: {}", user);
        otpService.save(user);
    }
}
