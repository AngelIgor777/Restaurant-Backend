package org.test.restaurant_service.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.test.restaurant_service.dto.request.translations.UiTranslationCreateDTO;
import org.test.restaurant_service.dto.request.translations.UiTranslationUpdateValueDTO;
import org.test.restaurant_service.dto.response.UiTranslationDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.UiTranslation;
import org.test.restaurant_service.mapper.UiTranslationMapper;
import org.test.restaurant_service.repository.UiTranslationRepository;
import org.test.restaurant_service.service.LanguageService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UiTranslationService {

    private final UiTranslationRepository uiTranslationRepository;
    private final UiTranslationMapper mapper;
    private final LanguageService languageService;

    @Transactional(readOnly = true)
    public List<UiTranslationDTO> getAllByLangId(Integer langId) {
        Language lang = languageService.getById(langId);
        return uiTranslationRepository.findAllByLanguage(lang)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional
    public UiTranslationDTO save(UiTranslationCreateDTO dto) {
        Language lang = languageService.getById(dto.getLangId());

        UiTranslation entity = uiTranslationRepository
                .findByKeyAndLanguage(dto.getKey(), lang)
                .orElseGet(() -> {
                    UiTranslation newEnt = new UiTranslation();
                    newEnt.setKey(dto.getKey());
                    newEnt.setLanguage(lang);
                    return newEnt;
                });
        entity.setValue(dto.getValue());
        UiTranslation saved = uiTranslationRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Transactional
    public UiTranslationDTO updateValueByLangIdAndKey(UiTranslationUpdateValueDTO dto) {
        Language lang = languageService.getById(dto.getLangId());

        UiTranslation entity = uiTranslationRepository
                .findByKeyAndLanguage(dto.getKey(), lang)
                .orElseThrow(() ->
                        new IllegalArgumentException("Translation not found for key=" + dto.getKey() + " and langId=" + dto.getLangId())
                );

        entity.setValue(dto.getValue());
        UiTranslation saved = uiTranslationRepository.save(entity);
        return mapper.toDto(saved);
    }


    @Transactional
    public void deleteByLangIdAndKey(String key, Integer langId) {
        Language lang = languageService.getById(langId);
        UiTranslation entity = uiTranslationRepository.findByKeyAndLanguage(key, lang)
                .orElseThrow(() -> new IllegalArgumentException("Translation not found for key=" + key + " and langId=" + langId));
        uiTranslationRepository.delete(entity);
    }

    public boolean existByKeyAndLanguage(String key, Language language) {
        return uiTranslationRepository.existsByKeyAndLanguage(key, language);
    }

}
