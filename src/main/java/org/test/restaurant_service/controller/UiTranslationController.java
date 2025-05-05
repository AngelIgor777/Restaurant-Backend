package org.test.restaurant_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.test.restaurant_service.dto.request.translations.UiTranslationCreateDTO;
import org.test.restaurant_service.dto.request.translations.UiTranslationUpdateValueDTO;
import org.test.restaurant_service.dto.response.UiTranslationDTO;
import org.test.restaurant_service.service.impl.UiTranslationService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ui-translations")
@RequiredArgsConstructor
public class UiTranslationController {

    private final UiTranslationService uiTranslationService;


    @GetMapping
    public List<UiTranslationDTO> getByLangId(@RequestParam("langId") Integer langId) {
        return uiTranslationService.getAllByLangId(langId);
    }

    @PostMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.CREATED)
    public UiTranslationDTO upsert(
            @Valid @RequestBody UiTranslationCreateDTO createDTO
    ) {
        return uiTranslationService.upsert(createDTO);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    public UiTranslationDTO updateTranslationValue(
            @Valid @RequestBody UiTranslationUpdateValueDTO updateDTO
    ) {
        return uiTranslationService.updateValueByLangIdAndKey(updateDTO);
    }

    @DeleteMapping
    @PreAuthorize("@securityService.userIsAdminOrModerator(@jwtServiceImpl.extractToken())")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteByLangIdAndKey(@RequestParam("key") String key,
                                     @RequestParam("langId") Integer langId) {
        uiTranslationService.deleteByLangIdAndKey(key, langId);
    }

}
