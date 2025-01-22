package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.OrderProductRequestDtoWithPayloadDto;

public interface OrderProductAndUserService {
    void createBulk(OrderProductRequestDtoWithPayloadDto requestDtoWithPayloadDto);

}
