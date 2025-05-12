package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.test.restaurant_service.dto.response.InfoResponseDTO;
import org.test.restaurant_service.dto.response.TableResponseDTO;
import org.test.restaurant_service.dto.response.FeatureStatusResponseDTO;
import org.test.restaurant_service.service.impl.FeatureService;
import org.test.restaurant_service.service.impl.TableService;


import java.util.List;

@RestController
@RequestMapping("/api/v1/info")
@RequiredArgsConstructor
public class InfoController {

    private final TableService tableService;
    private final FeatureService featureService;

    @GetMapping
    public ResponseEntity<InfoResponseDTO> getInfo() {
        List<TableResponseDTO> tables = tableService.getAll(Pageable.unpaged());
        List<FeatureStatusResponseDTO> features = featureService.getAllFeaturesStatus();
        InfoResponseDTO info = new InfoResponseDTO(tables, features);
        return ResponseEntity.ok(info);
    }
}
