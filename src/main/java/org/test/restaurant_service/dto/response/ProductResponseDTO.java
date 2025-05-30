package org.test.restaurant_service.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.test.restaurant_service.entity.Photo;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Data
public class ProductResponseDTO {
    protected Integer id;
    protected String name;
    protected String description;
    protected String typeName;
    private boolean available;
    protected BigDecimal price;
    protected LocalTime cookingTime;
    protected Integer quantity;
    protected String photoUrl;
    @JsonIgnore
    protected List<Photo> photos;
}