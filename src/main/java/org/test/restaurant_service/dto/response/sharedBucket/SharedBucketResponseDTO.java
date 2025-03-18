package org.test.restaurant_service.dto.response.sharedBucket;

import lombok.*;
import org.test.restaurant_service.entity.SharedBucket.SharedBucketStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SharedBucketResponseDTO {
    private Integer id;
    private UUID sessionUUID;
    private UUID userUUID;
    private SharedBucketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
