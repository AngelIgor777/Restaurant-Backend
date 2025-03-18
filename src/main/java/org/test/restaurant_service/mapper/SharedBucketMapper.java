package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.request.SharedBucketRequestDTO;
import org.test.restaurant_service.dto.response.sharedBucket.SharedBucketResponseDTO;
import org.test.restaurant_service.entity.SharedBucket;
import org.test.restaurant_service.mapper.helper.SharedBucketMapperHelper;


@Mapper(componentModel = "spring", uses = SharedBucketMapperHelper.class)
public interface SharedBucketMapper {

    SharedBucketMapper INSTANCE = Mappers.getMapper(SharedBucketMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sessionUUID", ignore = true)
    @Mapping(target = "user", source = "userUUID", qualifiedByName = "mapUser")
    SharedBucket toEntity(SharedBucketRequestDTO dto);

    @Mapping(target = "userUUID", source = "user.uuid")
    SharedBucketResponseDTO toResponseDto(SharedBucket entity);

}
