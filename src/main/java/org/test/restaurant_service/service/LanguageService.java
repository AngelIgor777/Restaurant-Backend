package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.request.LanguageRequestDTO;
import org.test.restaurant_service.dto.response.LanguageResponseDTO;
import org.test.restaurant_service.entity.Language;

import java.util.List;

public interface LanguageService {
    Language getLanguageByCode(String code);

    Language getById(Integer id);

    void setLanguage(Long chatId, String code);

    List<Language> getAll();

    Language create(Language language);

    Language update(Integer id, LanguageRequestDTO dto);

    void delete(Integer id);
}
