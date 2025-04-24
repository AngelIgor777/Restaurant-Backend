package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class TableStateOrders {
    private List<Integer> ordersId;
}
