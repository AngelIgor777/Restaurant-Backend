package org.test.restaurant_service.rabbitmq.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadAndPrintRequestDto;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.response.OtpResponseDto;
import org.test.restaurant_service.entity.Order;
import org.test.restaurant_service.service.OtpService;

@Slf4j
@Service
@RequiredArgsConstructor
public class RabbitMQJsonProducer {

    @Value("${rabbitmq.exchanges.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.queues.json.queue1.routingKey}")
    private String orderSavingRoutingKey;

    @Value("${rabbitmq.queues.json.queue3.routingKey}")
    private String orderBulkFromAdminRoutingKey;

    private final RabbitTemplate rabbitTemplate;
    private final OtpService otpService;

    public OtpResponseDto send(OrderProductWithPayloadRequestDto request) {
        return sendMessage(request, orderSavingRoutingKey);
    }

    public OtpResponseDto send(OrderProductWithPayloadAndPrintRequestDto request) {
        return sendMessage(request, orderBulkFromAdminRoutingKey);
    }

    private <T extends OrderProductWithPayloadRequestDto> OtpResponseDto sendMessage(T request, String routingKey) {
        OtpResponseDto otpResponseDto = new OtpResponseDto();
        if (request instanceof OrderProductWithPayloadAndPrintRequestDto) {
            OrderProductWithPayloadAndPrintRequestDto requestForPrintDto = (OrderProductWithPayloadAndPrintRequestDto) request;
            if (needsOtp(requestForPrintDto)) {
                String otp = otpService.generateOtpForOrder();
                request.setOtp(otp);
                otpResponseDto.setOtp(otp);
            }
        }

        rabbitTemplate.convertAndSend(exchangeName, routingKey, request);
        return otpResponseDto;
    }

    private boolean needsOtp(OrderProductWithPayloadAndPrintRequestDto request) {
        return request.getOrderStatus() == null || !Order.OrderStatus.CONFIRMED.equals(request.getOrderStatus());
    }
}
