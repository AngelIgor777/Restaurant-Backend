package org.test.restaurant_service.dto.response.sharedBucket;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SharedBucketProductPayloadResponseDto {
    private SharedBucketResponseDTO sharedBucket;
    private List<UserBucketResponseDto> usersResponseDTO;
}
