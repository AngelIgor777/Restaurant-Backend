package org.test.restaurant_service.dto.response;

import lombok.Data;

@Data
public class OrderProductResponseDTO {
    private Integer id;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
}