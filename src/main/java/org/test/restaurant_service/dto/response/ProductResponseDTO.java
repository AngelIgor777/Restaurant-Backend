package org.test.restaurant_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.test.restaurant_service.entity.Photo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class ProductResponseDTO {
    private Integer id;
    private String name;
    private String description;
    private String typeName;
    private BigDecimal price;
    private LocalTime cookingTime;
    private Integer quantity;
    @JsonIgnore
    private List<Photo> photos;
}