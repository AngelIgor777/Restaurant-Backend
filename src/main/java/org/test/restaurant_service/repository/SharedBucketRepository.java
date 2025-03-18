package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.SharedBucket;

public interface SharedBucketRepository extends JpaRepository<SharedBucket, Integer> {
}
