package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.OrderProductRequestDtoWithPayloadDto;
import org.test.restaurant_service.dto.response.OrderProductResponseDtoWithPayloadDto;

public interface OrderProductAndUserService {
    OrderProductResponseDtoWithPayloadDto createBulk(OrderProductRequestDtoWithPayloadDto requestDtoWithPayloadDto);

}
