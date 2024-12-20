package org.test.restaurant_service.dto.response;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String typeName;
    private BigDecimal price;
}