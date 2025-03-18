package org.test.restaurant_service.dto.response.sharedBucket;

import lombok.*;

import java.util.UUID;

@Data
public class SharedBucketProductResponseDTO {
    private Integer id;
    private Integer productId;
    private Integer sharedBucketId;
    private UUID userUUID;
    private Integer quantity;
}
