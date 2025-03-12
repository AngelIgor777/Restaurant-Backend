package org.test.restaurant_service.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.OrderProductRequestWithPayloadDto;
import org.test.restaurant_service.dto.request.UserRegistrationDTO;
import org.test.restaurant_service.dto.response.OtpResponseDto;
import org.test.restaurant_service.service.OtpService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQJsonProducer {

    @Value("${rabbitmq.exchanges.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queues.json.queue1.routingKey}")
    private String orderSavingRoutingKey;
    private final OtpService otpService;

    private final RabbitTemplate rabbitTemplate;

    public OtpResponseDto send(OrderProductRequestWithPayloadDto request) {
        String otp = otpService.generateOtpForOrder();
        request.setOtp(otp);
        log.debug("Отправка сообщения в RabbitMQ: {}", request);
        rabbitTemplate.convertAndSend(exchangeName, orderSavingRoutingKey, request);
        return new OtpResponseDto(otp);
    }

}
