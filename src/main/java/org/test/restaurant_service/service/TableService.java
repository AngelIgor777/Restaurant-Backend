package org.test.restaurant_service.service;

import org.springframework.data.domain.*;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.TableResponseDTO;

import java.util.List;

public interface TableService {
    Page<TableResponseDTO> getAll(Pageable pageable);

    List<TableResponseDTO> getAll();

    byte countAll();

    TableResponseDTO getById(Integer id);

    TableResponseDTO create(TableRequestDTO tableRequestDTO);

    TableResponseDTO update(Integer id, TableRequestDTO tableRequestDTO);

    void deleteById(Integer id);
}
