package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.test.restaurant_service.entity.SharedBucketProduct;
import org.test.restaurant_service.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SharedBucketProductRepository extends JpaRepository<SharedBucketProduct, Integer> {
    List<SharedBucketProduct> findAllByUser_Uuid(UUID uuid);

    List<SharedBucketProduct> findAllBySharedBucket_Id(Integer uuid);

    Optional<SharedBucketProduct> findBySharedBucket_IdAndUser_UuidAndProduct_Id(Integer sharedBucketId, UUID uuid,Integer productId);

    @Query("SELECT DISTINCT sbp.user FROM SharedBucketProduct sbp WHERE sbp.sharedBucket.id = :bucketId AND sbp.user IS NOT NULL")
    List<User> findDistinctUsersBySharedBucketId(@Param("bucketId") Integer bucketId);
}
