package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.OrderProductWithPayloadRequestDto;
import org.test.restaurant_service.dto.response.OrderProductResponseWithPayloadDto;

public interface OrderProductAndUserService {
    OrderProductResponseWithPayloadDto createBulk(OrderProductWithPayloadRequestDto requestDtoWithPayloadDto);

}
