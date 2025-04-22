package org.test.restaurant_service.dto.response.sharedBucket;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class UserBucketResponseDto {
    private UUID userUUID;
    private String firstName;
    private String photoUrl;
    private boolean confirmed;
    private List<ProductsForSharedBucketResponseDto> productsResponseDto;
}
