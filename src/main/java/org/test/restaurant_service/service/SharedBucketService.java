package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.SharedBucketProductRequestDTO;
import org.test.restaurant_service.dto.request.SharedBucketRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketProductPayloadResponseDto;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketResponseDTO;
import org.test.restaurant_service.dto.response.sharedBucket.UserBucketResponseDto;
import org.test.restaurant_service.entity.SharedBucket;

import java.util.List;
import java.util.UUID;

public interface SharedBucketService {
    SharedBucketResponseDTO createSharedBucket(SharedBucketRequestDTO dto);

    SharedBucketProductPayloadResponseDto getSharedBucketById(Integer id, UUID userUUID);

    SharedBucket get(Integer id);

    List<SharedBucketResponseDTO> getAllSharedBuckets();


    void deleteSharedBucket(Integer id);

    void addProduct(UUID sessionUUID, SharedBucketProductRequestDTO sharedBucketRequestDTO);

    List<UserBucketResponseDto> getUsersInfoInSharedBucketById(Integer bucketId);

    void confirmUser(UUID sessionUUID, UUID userUUID);

    boolean allUsersConfirmed(UUID sessionUUID);
}
