package org.test.restaurant_service.service.impl.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.entity.Language;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvailableLanguagesCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY = "availableLanguages";


    public void saveLanguageToAvailable(Language language) {
        List<Language> languagesAvailable = getLanguagesAvailable();
        if(!languagesAvailable.contains(language)) {
            languagesAvailable.add(language);
        }
        saveLanguages(languagesAvailable);
    }

    public List<Language> getLanguagesAvailable() {
        Object object = redisTemplate.opsForValue().get(KEY);
        if (object == null) {
            return new ArrayList<>();
        }
        return objectMapper.convertValue(object, new TypeReference<List<Language>>() {
        });
    }

    public void deleteLanguageFromAvailable(Language language) {
        List<Language> languagesAvailable = getLanguagesAvailable();
        languagesAvailable.remove(language);
        saveLanguages(languagesAvailable);
    }

    private void saveLanguages(List<Language> languagesAvailable) {
        redisTemplate.opsForValue().set(KEY, languagesAvailable);
    }

    public boolean langISAvailable(Integer langId) {
        return getLanguagesAvailable().stream()
                .map(Language::getId).toList().contains(langId);
    }
}
