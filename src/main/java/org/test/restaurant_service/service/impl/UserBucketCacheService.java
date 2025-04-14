package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.OrderProductRequestDTO;
import org.test.restaurant_service.dto.request.order.OrderProductWithPayloadRequestDto;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class UserBucketCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public void saveProductToBucket(Long chatId, Integer productId, Integer quantity) {
        List<OrderProductRequestDTO> productsId = getProductsInBucket(chatId);
        AtomicBoolean isUnique = new AtomicBoolean(true);
        if (productsId == null) {
            productsId = new ArrayList<>();
        } else {
            productsId
                    .forEach(product -> {
                        if (product.getProductId().equals(productId)) {
                            product.setQuantity(product.getQuantity() + quantity);
                            isUnique.set(false);
                        }
                    });
        }
        if (isUnique.get()) {
            OrderProductRequestDTO productInBucketInfo = new OrderProductRequestDTO();
            productInBucketInfo.setProductId(productId);
            productInBucketInfo.setQuantity(quantity);
            productsId.add(productInBucketInfo);
        }
        redisTemplate.opsForValue().set("bucket:" + chatId, productsId, Duration.ofMinutes(30));
    }

    public List<OrderProductRequestDTO> getProductsInBucket(Long chatId) {
        return (List<OrderProductRequestDTO>) redisTemplate.opsForValue().get("bucket:" + chatId);
    }

    public void deleteBucket(Long chatId) {
        redisTemplate.delete("bucket:" + chatId);
    }


    public void saveOrder(Long chatId, OrderProductWithPayloadRequestDto order) {
        redisTemplate.opsForValue().set("tgBucketOrder:" + chatId, order, Duration.ofMinutes(30));
    }

    public OrderProductWithPayloadRequestDto getOrder(Long chatId) {
        return (OrderProductWithPayloadRequestDto) redisTemplate.opsForValue().get("tgBucketOrder:" + chatId);
    }

    public void deleteOrder(Long chatId) {
        redisTemplate.delete("tgBucketOrder:" + chatId);
    }
}
