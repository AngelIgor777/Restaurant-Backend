package org.test.restaurant_service.mapper;

import org.mapstruct.*;
import org.test.restaurant_service.dto.request.AddressRequestDTO;
import org.test.restaurant_service.dto.response.AddressResponseDTO;
import org.test.restaurant_service.entity.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "id", ignore = true)
    AddressResponseDTO toAddressResponseDTO(AddressRequestDTO addressRequestDTO);

    @Mapping(target = "userUUID", ignore = true)
    AddressResponseDTO toResponseDto(Address address);


    @Mapping(target = "user", ignore = true)
    @Mapping(target = "id", ignore = true)
    Address toEntity(AddressRequestDTO addressRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateAddressFromDto(AddressRequestDTO addressRequestDTO, @MappingTarget Address address);

}
