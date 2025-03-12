package org.test.restaurant_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@Builder
public class OrderResponseDTO {
    private Integer id;
    private String status;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private LocalTime totalCookingTime;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String otp;
    private List<ProductResponseDTO> products;
}
