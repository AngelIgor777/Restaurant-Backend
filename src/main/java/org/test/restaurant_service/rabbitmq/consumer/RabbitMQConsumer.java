package org.test.restaurant_service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;
import org.test.restaurant_service.service.OrderProductAndUserService;
import com.rabbitmq.client.Channel;

@Slf4j
@Component
public class RabbitMQConsumer {

    private final OrderProductAndUserService orderProductAndUserService;

    public RabbitMQConsumer( OrderProductAndUserService orderProductAndUserService) {
        this.orderProductAndUserService = orderProductAndUserService;
    }

    @RabbitListener(queues = "#{rabbitMQConfig.getOrderSavingQueue()}", ackMode = "MANUAL")
    public void consumeUserRegistration(Message message, Channel channel) {
        try {
            OrderProductRequestWithPayloadDto orderProductRequestWithPayloadDto = deserializeMessage(message);

            log.debug("Consumed message: {}", orderProductRequestWithPayloadDto);

            orderProductAndUserService.createBulk(orderProductRequestWithPayloadDto);
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

    private OrderProductRequestWithPayloadDto deserializeMessage(Message message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(message.getBody(), OrderProductRequestWithPayloadDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message");
        }
    }
}
