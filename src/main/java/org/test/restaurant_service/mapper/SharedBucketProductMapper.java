package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.request.SharedBucketProductRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.ProductsForSharedBucketResponseDto;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketProductResponseDTO;
import org.test.restaurant_service.entity.SharedBucketProduct;
import org.test.restaurant_service.mapper.helper.SharedBucketMapperHelper;

@Mapper(componentModel = "spring", uses = SharedBucketMapperHelper.class)
public interface SharedBucketProductMapper {

    SharedBucketProductMapper INSTANCE = Mappers.getMapper(SharedBucketProductMapper.class);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "product", source = "productId", qualifiedByName = "mapProduct")
    @Mapping(target = "sharedBucket", source = "sharedBucketId", qualifiedByName = "mapSharedBucket")
    @Mapping(target = "user", source = "userUUID", qualifiedByName = "mapUser")
    SharedBucketProduct toEntity(SharedBucketProductRequestDTO dto);


    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sharedBucketId", source = "sharedBucket.id")
    @Mapping(target = "userUUID", source = "user.uuid")
    SharedBucketProductResponseDTO toResponseDto(SharedBucketProduct entity);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "sharedBucketId", source = "sharedBucket.id")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "userUUID", source = "user.uuid")
//    @Mapping(target = "photoUrl", source = "product.photos", qualifiedByName = "mapPhoto")
    ProductsForSharedBucketResponseDto toResponseForSharedBucketResponseDto(SharedBucketProduct entity);


}
