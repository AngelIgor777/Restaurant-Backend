package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.test.restaurant_service.entity.Photo;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Integer> {
    List<Photo> findAllByProductId(Integer productId);

    Optional<Photo> findByUrl(String fileUrl);

    List<Photo> findAllByUrl(String fileUrl);
}
