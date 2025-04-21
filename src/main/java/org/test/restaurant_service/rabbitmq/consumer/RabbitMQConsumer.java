package org.test.restaurant_service.rabbitmq.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadAndPrintRequestDto;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.service.OrderProductAndUserService;
import com.rabbitmq.client.Channel;

@Slf4j
@Component
public class RabbitMQConsumer {

    private final OrderProductAndUserService orderProductAndUserService;
    private final ObjectMapper objectMapper;

    public RabbitMQConsumer(OrderProductAndUserService orderProductAndUserService, ObjectMapper objectMapper) {
        this.orderProductAndUserService = orderProductAndUserService;
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "#{rabbitMQConfig.getOrderSavingQueue()}", ackMode = "MANUAL")
    public void consumeOrderCreation(Message message, Channel channel) {
        try {
            OrderProductWithPayloadRequestDto orderProductWithPayloadRequestDto = objectMapper.readValue(message.getBody(), OrderProductWithPayloadRequestDto.class);

            log.debug("Consumed message: {}", orderProductWithPayloadRequestDto);

            orderProductAndUserService.createBulk(orderProductWithPayloadRequestDto);
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

    @RabbitListener(queues = "#{rabbitMQConfig.getOrderBulkFromAdminQueue()}", ackMode = "MANUAL")
    public void consumeUserRegistration(Message message, Channel channel) {
        try {
            OrderProductWithPayloadAndPrintRequestDto orderProductWithPayloadRequestDto = objectMapper.readValue(message.getBody(), OrderProductWithPayloadAndPrintRequestDto.class);

            log.debug("Consumed message: {}", orderProductWithPayloadRequestDto);

            orderProductAndUserService.createBulk(orderProductWithPayloadRequestDto);
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

}
