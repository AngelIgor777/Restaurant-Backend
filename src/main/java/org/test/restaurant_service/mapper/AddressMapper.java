package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressMapper INSTANCE = Mappers.getMapper(AddressMapper.class);

    @Mapping(target = "id", ignore = true)
    AddressResponseDTO toAddressResponseDTO(AddressRequestDTO addressRequestDTO);

    @Mapping(target = "userUUID", source = "user.uuid")
    AddressResponseDTO toResponseDto(Address address);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressRequestDTO addressRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressFromDto(AddressRequestDTO addressRequestDTO, @MappingTarget Address address);

}
