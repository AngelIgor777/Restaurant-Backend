package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.controller.websocket.WebSocketSender;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.request.table.OpenTables;
import org.test.restaurant_service.dto.response.TableResponseDTO;
import org.test.restaurant_service.entity.Table;
import org.test.restaurant_service.mapper.TableMapper;
import org.test.restaurant_service.repository.TableRepository;
import org.test.restaurant_service.service.impl.cache.TableCacheService;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableService {

    private final TableRepository tableRepository;
    private final TableMapper tableMapper;
    private final TableCacheService tableCacheService;
    private final WebSocketSender webSocketSender;

    public List<TableResponseDTO> getAll(Pageable pageable) {
        OpenTables closedTables = tableCacheService.getOpenTables();
        Set<Integer> ids = closedTables.getIds();
        return tableRepository.findAll(pageable).getContent()
                .stream().map(table -> {
                    TableResponseDTO responseDTO = tableMapper.toResponseDTO(table);
                    if (ids.contains(table.getId())) {
                        responseDTO.setOpen(true);
                    }
                    return responseDTO;
                }).toList();
    }

    public List<TableResponseDTO> getAll() {
        return tableRepository.findAll()
                .stream().map(tableMapper::toResponseDTO)
                .toList();
    }

    public byte countAll() {
        return (byte) tableRepository.count();
    }

    public TableResponseDTO getById(Integer id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
        return tableMapper.toResponseDTO(table);
    }

    public TableResponseDTO create(TableRequestDTO tableRequestDTO) {
        Table table = tableMapper.toEntity(tableRequestDTO);
        Table savedTable = tableRepository.save(table);
        return tableMapper.toResponseDTO(savedTable);
    }

    public TableResponseDTO update(Integer id, TableRequestDTO tableRequestDTO) {
        Table table = tableRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with id: " + id));
        tableMapper.updateEntityFromRequestDTO(tableRequestDTO, table);
        Table updatedTable = tableRepository.save(table);
        return tableMapper.toResponseDTO(updatedTable);
    }

    public void deleteById(Integer id) {
        if (!tableRepository.existsById(id)) {
            throw new EntityNotFoundException("Table not found with id: " + id);
        }
        tableRepository.deleteById(id);
    }

    public TableResponseDTO openTable(Integer id) {
        UUID uuid = tableCacheService.addTableToOpen(id);
        TableResponseDTO table = getById(id);
        table.setSessionUUID(uuid);
        table.setOpen(true);

        OpenTables openTables = tableCacheService.getOpenTables();

        webSocketSender.sendOpenTables(openTables);
        return table;
    }

    public Table getByNumber(Integer tableNumber) {
        return tableRepository.findTablesByNumber(tableNumber)
                .orElseThrow(() -> new EntityNotFoundException("Table not found with number " + tableNumber));
    }

}
