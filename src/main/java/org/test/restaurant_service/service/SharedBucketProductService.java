package org.test.restaurant_service.service;

import org.test.restaurant_service.entity.SharedBucketProduct;
import org.test.restaurant_service.entity.User;

import java.util.List;
import java.util.UUID;

public interface SharedBucketProductService {

    List<SharedBucketProduct> findAllSharedBucketProductsByUserUUID(UUID uuid);

    List<SharedBucketProduct> findAllSharedBucketProductsByBucketId(Integer bucketId);

    List<User> findUsersBySharedBucketId(Integer bucketId);

    SharedBucketProduct save(SharedBucketProduct sharedBucketProduct);


}



