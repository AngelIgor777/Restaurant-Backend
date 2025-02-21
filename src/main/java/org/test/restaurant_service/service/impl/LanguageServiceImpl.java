package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.repository.LanguageRepository;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.TelegramUserService;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {
    private final LanguageRepository languageRepository;

    private final TelegramUserService telegramUserService;

    @Override
    public Language getLanguageByCode(String code) {
        return languageRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
    }

    @Override
    public void setLanguage(Long chatId, String code) {
        Language language = languageRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
        TelegramUserEntity user = telegramUserService.getByChatId(chatId);
        user.setLanguage(language);
        telegramUserService.save(user);
    }
}