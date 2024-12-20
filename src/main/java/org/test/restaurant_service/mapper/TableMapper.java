package org.test.restaurant_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.TableResponseDTO;
import org.test.restaurant_service.entity.Table;

@Mapper(componentModel = "spring")
public interface TableMapper {

    Table toEntity(TableRequestDTO tableRequestDTO);

    TableResponseDTO toResponseDTO(Table table);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequestDTO(TableRequestDTO requestDTO, @MappingTarget Table table);
}
