package org.test.restaurant_service.service.impl;


import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.request.LanguageRequestDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.entity.TelegramUserEntity;
import org.test.restaurant_service.repository.LanguageRepository;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.TelegramUserService;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final TelegramUserService telegramUserService;

    public LanguageServiceImpl(LanguageRepository languageRepository, TelegramUserService telegramUserService) {
        this.languageRepository = languageRepository;
        this.telegramUserService = telegramUserService;
    }

    @Override
    @Cacheable(cacheNames = "lang", key = "#code+'-lang'")
    public Language getLanguageByCode(String code) {
        return languageRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
    }

    @Override
    public Language getById(Integer id) {
        return languageRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Language not found"));
    }

    @Override
    public void setLanguage(Long chatId, String code) {
        Language language = languageRepository.findByCode(code)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
        TelegramUserEntity user = telegramUserService.getByChatId(chatId);
        user.setLanguage(language);
        telegramUserService.save(user);
    }

    @Override
    @Cacheable(cacheNames = "languages", key = "'all'")
    public List<Language> getAll() {
        return languageRepository.findAll();
    }


    @Override
    @CacheEvict(value = "languages", allEntries = true)
    public Language create(Language language) {
        return languageRepository.save(language);
    }

    @Override
    @CacheEvict(value = "languages", allEntries = true)
    public Language update(Integer id, LanguageRequestDTO dto) {
        Language lang = languageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Language not found"));
        if (dto.code() != null) lang.setCode(dto.code());
        if (dto.name() != null) lang.setName(dto.name());
        return languageRepository.save(lang);
    }

    @Override
    @CacheEvict(value = "languages", allEntries = true)
    public void delete(Integer id) {
        if (!languageRepository.existsById(id)) {
            throw new EntityNotFoundException("Language not found");
        }
        languageRepository.deleteById(id);
    }


}