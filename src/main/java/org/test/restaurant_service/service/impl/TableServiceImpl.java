package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.TableResponseDTO;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.mapper.TableMapper;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.TableService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;

    @Override
    public Page<TableResponseDTO> getAll(Pageable pageable) {
        return tableRepository.findAll(pageable)
                .map(tableMapper::toResponseDTO);
    }

    @Override
    public TableResponseDTO getById(Integer id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
        return tableMapper.toResponseDTO(table);
    }

    @Override
    public TableResponseDTO create(TableRequestDTO tableRequestDTO) {
        Table table = tableMapper.toEntity(tableRequestDTO);
        Table savedTable = tableRepository.save(table);
        return tableMapper.toResponseDTO(savedTable);
    }

    @Override
    public TableResponseDTO update(Integer id, TableRequestDTO tableRequestDTO) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
        tableMapper.updateEntityFromRequestDTO(tableRequestDTO, table);
        Table updatedTable = tableRepository.save(table);
        return tableMapper.toResponseDTO(updatedTable);
    }

    @Override
    public void deleteById(Integer id) {
        if (!tableRepository.existsById(id)) {
            throw new EntityNotFoundException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }
}
