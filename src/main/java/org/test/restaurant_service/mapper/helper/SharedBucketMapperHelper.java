package org.test.restaurant_service.mapper.helper;


import org.mapstruct.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.SharedBucket;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.repository.SharedBucketRepository;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class SharedBucketMapperHelper {

    private static final Logger log = LoggerFactory.getLogger(SharedBucketMapperHelper.class);
    private final ProductService productService;
    private final UserService userService;
    private final SharedBucketRepository sharedBucketRepository;


    public SharedBucketMapperHelper(@Qualifier("productServiceWithS3Impl") ProductService productService,
                                    UserService userService, SharedBucketRepository sharedBucketRepository) {
        this.productService = productService;
        this.userService = userService;
        this.sharedBucketRepository = sharedBucketRepository;
    }

    @Named("mapProduct")
    public Product mapProduct(Integer productId) {
        if (productId == null) return null;
        return productService.getSimpleById(productId);
    }

    @Named("mapSharedBucket")
    public SharedBucket mapSharedBucket(Integer sharedBucketId) {
        if (sharedBucketId == null) return null;
        return sharedBucketRepository.getById(sharedBucketId);
    }

    @Named("mapUser")
    public User mapUser(UUID userUUID) {
        if (userUUID == null) return null;
        return userService.findByUUID(userUUID);
    }

    @Named("mapPhoto")
    public String mapPhoto(List<Photo> photos) {
        Optional<Photo> any = photos
                .stream().findAny();
        if (any.isPresent()) {
            return any.get().getUrl();
        } else {
            log.warn("Not exists photo for product");
        }
        return "";
    }
}
