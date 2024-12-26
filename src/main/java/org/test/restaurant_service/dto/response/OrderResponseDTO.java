package org.test.restaurant_service.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Integer id;
    private Integer tableId;
    private String status;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private LocalTime totalCookingTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ProductResponseDTO> products;
}
