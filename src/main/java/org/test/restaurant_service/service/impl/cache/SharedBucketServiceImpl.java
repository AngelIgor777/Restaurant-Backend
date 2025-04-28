package org.test.restaurant_service.service.impl.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.SharedBucketProductRequestDTO;
import org.test.restaurant_service.dto.request.SharedBucketRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.ProductsForSharedBucketResponseDto;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketProductPayloadResponseDto;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketResponseDTO;
import org.test.restaurant_service.dto.response.sharedBucket.UserBucketResponseDto;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.SharedBucket;
import org.test.restaurant_service.entity.SharedBucketProduct;
import org.test.restaurant_service.entity.User;
import org.test.restaurant_service.mapper.SharedBucketMapper;
import org.test.restaurant_service.mapper.SharedBucketProductMapper;
import org.test.restaurant_service.repository.SharedBucketRepository;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.SharedBucketProductService;
import org.test.restaurant_service.service.SharedBucketService;
import org.test.restaurant_service.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SharedBucketServiceImpl implements SharedBucketService {

    private static final String BUCKET_KEY_PREFIX = "sharedBucket:";
    private static final String CONFIRMATION_KEY_PREFIX = "confirmations:";


    private final SharedBucketRepository sharedBucketRepository;
    private final SharedBucketMapper sharedBucketMapper;
    private final SharedBucketProductService sharedBucketProductService;
    private final ProductService productService;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;

    public SharedBucketServiceImpl(SharedBucketRepository sharedBucketRepository, SharedBucketMapper sharedBucketMapper, SharedBucketProductService sharedBucketProductService, @Qualifier("productServiceWithS3Impl") ProductService productService, UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.sharedBucketRepository = sharedBucketRepository;
        this.sharedBucketMapper = sharedBucketMapper;
        this.sharedBucketProductService = sharedBucketProductService;
        this.productService = productService;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public SharedBucketResponseDTO createSharedBucket(SharedBucketRequestDTO dto) {
        SharedBucket sharedBucket = sharedBucketMapper.toEntity(dto);
        sharedBucket.setStatus(SharedBucket.SharedBucketStatus.ACTIVE);
        sharedBucket.setSessionUUID(UUID.randomUUID());
        sharedBucket.setCreatedAt(LocalDateTime.now());
        SharedBucket savedBucket = sharedBucketRepository.save(sharedBucket);
        return sharedBucketMapper.toResponseDto(savedBucket);
    }


    public SharedBucketResponseDTO createSharedBucket(SharedBucket sharedBucket) {
        SharedBucket savedBucket = sharedBucketRepository.save(sharedBucket);
        return sharedBucketMapper.toResponseDto(savedBucket);
    }

    @Transactional(readOnly = true)
    @Override
    public SharedBucketProductPayloadResponseDto getSharedBucketById(Integer id, UUID userUUID) {
        SharedBucket sharedBucket = get(id);
        UUID sessionUUID = sharedBucket.getSessionUUID();
        SharedBucketResponseDTO responseDto = SharedBucketMapper.INSTANCE.toResponseDto(sharedBucket);
        List<User> usersInSharedBucket = sharedBucketProductService.findUsersBySharedBucketId(sharedBucket.getId());
        List<UserBucketResponseDto> usersInBucketInfo = getUsersInfoInSharedBucket(usersInSharedBucket);
        if (userUUID != null && !userUUID.toString().isEmpty()) {
            User user = userService.findByUUID(userUUID);
            String key = BUCKET_KEY_PREFIX + sessionUUID;
            redisTemplate.opsForSet().add(key, user.getUuid().toString());
            redisTemplate.delete(CONFIRMATION_KEY_PREFIX + sessionUUID);
        }
        return new SharedBucketProductPayloadResponseDto(responseDto, usersInBucketInfo);
    }

    public List<UserBucketResponseDto> getUsersInfoInSharedBucket(List<User> usersInSharedBucket) {
        return usersInSharedBucket.stream()
                .map(user -> {
                    List<ProductsForSharedBucketResponseDto> userProducts = sharedBucketProductService.findAllSharedBucketProductsByUserUUID(user.getUuid())
                            .stream()
                            .map(SharedBucketProductMapper.INSTANCE::toResponseForSharedBucketResponseDto).toList();

                    UserBucketResponseDto userBucketResponseDto = new UserBucketResponseDto();
                    userBucketResponseDto.setUserUUID(user.getUuid());
                    userBucketResponseDto.setFirstName(user.getTelegramUserEntity().getFirstname());
                    userBucketResponseDto.setPhotoUrl(user.getTelegramUserEntity().getPhotoUrl());
                    userBucketResponseDto.setProductsResponseDto(userProducts);
                    return userBucketResponseDto;
                }).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserBucketResponseDto> getUsersInfoInSharedBucketById(Integer bucketId) {
        List<User> usersBySharedBucketId = sharedBucketProductService.findUsersBySharedBucketId(bucketId);
        return getUsersInfoInSharedBucket(usersBySharedBucketId);
    }


    @Override
    public SharedBucket get(Integer id) {
        return sharedBucketRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SharedBucket not found with id: " + id));
    }

    @Override
    public List<SharedBucketResponseDTO> getAllSharedBuckets() {
        return sharedBucketRepository.findAll().stream()
                .map(sharedBucketMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteSharedBucket(Integer id) {
        SharedBucket sharedBucket = get(id);
        sharedBucketRepository.delete(sharedBucket);
    }

    @Override
    public void addProduct(UUID sessionUUID, SharedBucketProductRequestDTO sharedBucketRequestDTO) {
        SharedBucketProduct sharedBucketProduct = new SharedBucketProduct();
        User user = userService.findByUUID(sharedBucketRequestDTO.getUserUUID());
        Product product = productService.getSimpleById(sharedBucketRequestDTO.getProductId());
        sharedBucketProduct.setProduct(product);
        sharedBucketProduct.setUser(user);
        sharedBucketProduct.setSharedBucket(get(sharedBucketRequestDTO.getSharedBucketId()));
        sharedBucketProduct.setQuantity(sharedBucketRequestDTO.getQuantity());

        sharedBucketProductService.save(sharedBucketProduct);
    }


    public void confirmUser(UUID sessionUUID, UUID userUUID) {
        String key = CONFIRMATION_KEY_PREFIX + sessionUUID;
        redisTemplate.opsForSet().add(key, userUUID);
    }

    public boolean allUsersConfirmed(UUID sessionUUID) {
        String confirmKey = CONFIRMATION_KEY_PREFIX + sessionUUID;
        String bucketKey = BUCKET_KEY_PREFIX + sessionUUID;

        Long confirmedCount = redisTemplate.opsForSet().size(confirmKey);
        Long totalUsers = redisTemplate.opsForSet().size(bucketKey);


        log.debug("Confirmed count is SESSION '{}' - '{}'", sessionUUID, confirmedCount);
        log.debug("Total users is SESSION '{}' - '{}'", sessionUUID, totalUsers);
        return confirmedCount != null && confirmedCount.equals(totalUsers);
    }
}
