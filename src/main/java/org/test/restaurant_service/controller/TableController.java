package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.TableRequestDTO;
import org.test.restaurant_service.dto.response.TableResponseDTO;
import org.test.restaurant_service.service.impl.TableOrderScoreService;
import org.test.restaurant_service.service.impl.TableService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tables")
@RequiredArgsConstructor
public class TableController {

    private final TableService tableService;
    private final TableOrderScoreService tableOrderScoreService;

    @GetMapping
    public List<TableResponseDTO> getAll(Pageable pageable) {
        return tableService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public TableResponseDTO getById(@PathVariable Integer id) {
        return tableService.getById(id);
    }

    @PostMapping("/open/{tableId}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public TableResponseDTO closeTable(@PathVariable Integer tableId) {
        return tableService.openTable(tableId);
    }

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @ResponseStatus(HttpStatus.CREATED)
    public TableResponseDTO create(@RequestBody TableRequestDTO tableRequestDTO) {
        return tableService.create(tableRequestDTO);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public TableResponseDTO update(@PathVariable Integer id, @RequestBody TableRequestDTO tableRequestDTO) {
        return tableService.update(id, tableRequestDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        tableService.deleteById(id);
    }


    @GetMapping("/scores")
    @PreAuthorize("@securityService.userIsAdminOrModerator(authentication)")
    public List<String> getScores() {
        List<String> uuiDs = tableOrderScoreService.getUUIDs();
        return uuiDs;
    }
}
