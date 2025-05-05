package org.test.restaurant_service.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.LanguageRequestDTO;
import org.test.restaurant_service.dto.response.LanguageResponseDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.mapper.LanguageMapper;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.impl.AvailableLanguageService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
@Validated
public class LanguageController {

    private final LanguageService languageService;
    private final LanguageMapper languageMapper;
    private final AvailableLanguageService availableLanguageService;

    @GetMapping
    public List<LanguageResponseDTO> getAll() {
        return languageService.getAll().stream()
                .map(languageMapper::toDto)
                .collect(Collectors.toList());
    }

    @PostMapping("/run-check")
    public void runCheckLanguages() {
        availableLanguageService.checkAvailableLanguages();
    }

    @PostMapping
    public ResponseEntity<LanguageResponseDTO> create(@RequestBody @Valid LanguageRequestDTO dto) {
        Language saved = languageService.create(languageMapper.toEntity(dto));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(languageMapper.toDto(saved));
    }

    @PatchMapping("/{id}")
    public LanguageResponseDTO update(@PathVariable Integer id,
                                      @RequestBody @Valid LanguageRequestDTO dto) {
        Language updated = languageService.update(id, dto);
        return languageMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        languageService.delete(id);
    }
}