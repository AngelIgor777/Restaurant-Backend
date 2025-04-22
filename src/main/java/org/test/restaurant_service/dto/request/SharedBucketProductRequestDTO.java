package org.test.restaurant_service.dto.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharedBucketProductRequestDTO {
    private Integer productId;
    private Integer quantity;
    private UUID userUUID;
    private Integer sharedBucketId;
}
