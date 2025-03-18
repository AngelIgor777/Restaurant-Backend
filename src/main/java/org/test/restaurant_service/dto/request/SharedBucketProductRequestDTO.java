package org.test.restaurant_service.dto.request;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedBucketProductRequestDTO {
    private Integer productId;
    private Integer sharedBucketId;
    private UUID userUUID;
    private Integer quantity;
}
