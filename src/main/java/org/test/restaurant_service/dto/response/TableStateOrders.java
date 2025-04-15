package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TableStateOrders {
    private int count;
    private List<OrderProductResponseWithPayloadDto> orders;
}
