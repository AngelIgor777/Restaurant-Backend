package org.test.restaurant_service.dto.response.sharedBucket;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductsForSharedBucketResponseDto {
    private Integer id;
    private Integer productId;
    private Integer sharedBucketId;
    private UUID userUUID;
    private String name;
    private BigDecimal price;
    private Integer quantity;
    private String photoUrl;
}
