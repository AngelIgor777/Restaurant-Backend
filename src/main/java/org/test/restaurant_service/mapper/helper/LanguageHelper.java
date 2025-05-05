package org.test.restaurant_service.mapper.helper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import org.test.restaurant_service.service.impl.cache.AvailableLanguagesCacheService;

@Component
public class LanguageHelper {

    private final AvailableLanguagesCacheService languagesCacheService;

    public LanguageHelper(AvailableLanguagesCacheService languagesCacheService) {
        this.languagesCacheService = languagesCacheService;
    }

    @Named("langIsAvailable")
    public boolean langIsAvailable(Integer langId) {
        return languagesCacheService.langISAvailable(langId);
    }
}
