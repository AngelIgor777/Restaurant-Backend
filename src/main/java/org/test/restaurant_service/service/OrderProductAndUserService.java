package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;

public interface OrderProductAndUserService {
    <T extends OrderProductWithPayloadRequestDto> void createOrder(T orderRequestDtoWithPayloadDto);
}
