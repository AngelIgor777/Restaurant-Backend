package org.test.restaurant_service.service;

import org.test.restaurant_service.dto.response.LanguageResponseDTO;
import org.test.restaurant_service.entity.Language;

public interface LanguageService {
    Language getLanguageByCode(String code);
    void setLanguage(Long chatId, String code);
}
