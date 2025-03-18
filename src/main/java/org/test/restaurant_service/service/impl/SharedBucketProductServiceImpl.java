package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.SharedBucketProduct;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.SharedBucketProductRepository;
import org.test.restaurant_service.service.SharedBucketProductService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SharedBucketProductServiceImpl implements SharedBucketProductService {

    private final SharedBucketProductRepository sharedBucketProductRepository;

    @Override
    public List<SharedBucketProduct> findAllSharedBucketProductsByUserUUID(UUID uuid) {
        return sharedBucketProductRepository.findAllByUser_Uuid(uuid);
    }

    //todo
    @Override
    public List<SharedBucketProduct> findAllSharedBucketProductsByBucketId(Integer bucketId) {
        return sharedBucketProductRepository.findAllBySharedBucket_Id(bucketId);
    }

    @Override
    public List<User> findUsersBySharedBucketId(Integer bucketId) {
        return sharedBucketProductRepository.findDistinctUsersBySharedBucketId(bucketId);
    }

    @Override
    public SharedBucketProduct save(SharedBucketProduct sharedBucketProduct) {
        Optional<SharedBucketProduct> exist = sharedBucketProductRepository
                .findBySharedBucket_IdAndUser_UuidAndProduct_Id(sharedBucketProduct.getSharedBucket().getId(),
                        sharedBucketProduct.getUser().getUuid(),
                        sharedBucketProduct.getProduct().getId());
        if (exist.isPresent()) {
            SharedBucketProduct product = exist.get();
            product.setQuantity(product.getQuantity() + sharedBucketProduct.getQuantity());
            return sharedBucketProductRepository.save(product);
        }
        return sharedBucketProductRepository.save(sharedBucketProduct);

    }

}
