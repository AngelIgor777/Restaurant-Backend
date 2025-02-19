package org.test.restaurant_service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.service.OrderProductAndUserService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.impl.TelegramUserServiceImpl;
import com.rabbitmq.client.Channel;

@Slf4j
@Component
public class RabbitMQConsumer {

    private final ProductService productService;
    private final OrderProductAndUserService orderProductAndUserService;

    public RabbitMQConsumer(TelegramUserServiceImpl telegramUserService, @Qualifier("productServiceImpl") ProductService productService, OrderProductAndUserService orderProductAndUserService) {
        this.productService = productService;
        this.orderProductAndUserService = orderProductAndUserService;
    }

    /**
     * Метод для обработки сообщений из RabbitMQ.
     *
     * @param message сообщение, полученное из очереди
     * @param channel канал RabbitMQ для управления подтверждениями
     */
    @RabbitListener(queues = "#{rabbitMQConfig.getOrderSavingQueue()}", ackMode = "MANUAL")
    public void consumeUserRegistration(Message message, Channel channel) {
        try {
            // Десериализация сообщения
            OrderProductRequestWithPayloadDto orderProductRequestWithPayloadDto = deserializeUserRegistrationDTOMessage(message);

            log.info("Consumed User message: {}", orderProductRequestWithPayloadDto);

            // Обработка сообщения
            orderProductAndUserService.createBulk(orderProductRequestWithPayloadDto);
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

//
//    @RabbitListener(queues = "#{rabbitMQConfig.getJsonProductSaveQueue()}", ackMode = "MANUAL")
//    public void consumeProductSave(Message message, Channel channel) {
//        try {
//            // Десериализация сообщения
//            ProductRequestDTO productRequestDTO = deserializeProductRequestDTOMessage(message);
//
//            log.info("Consumed Product message: {}", productRequestDTO);
//
//            // Обработка сообщения
//            productService.create(productRequestDTO);
//
//            // Подтверждаем успешную обработку
//            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//        } catch (Exception e) {
//            log.error("Error processing message: {}", e.getMessage(), e);
//
//            try {
//                // Отклоняем сообщение и удаляем его из очереди
//                channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//            } catch (Exception ex) {
//                log.error("Failed to reject message: {}", ex.getMessage(), ex);
//            }
//        }
//    }


    private OrderProductRequestWithPayloadDto deserializeUserRegistrationDTOMessage(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getBody(), OrderProductRequestWithPayloadDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message");
        }
    }

    private ProductRequestDTO deserializeProductRequestDTOMessage(Message message) {
        // Логика десериализации сообщения из JSON в объект UserRegistrationDTO
        // Например, используя ObjectMapper из Jackson
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getBody(), ProductRequestDTO.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message");
        }
    }
}
