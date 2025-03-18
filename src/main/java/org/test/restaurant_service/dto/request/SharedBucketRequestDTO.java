package org.test.restaurant_service.dto.request;

import lombok.*;

import java.util.UUID;

@Data
public class SharedBucketRequestDTO {
    private UUID userUUID;
}
