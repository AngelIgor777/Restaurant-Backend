package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductIdsResponse;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
import org.test.restaurant_service.dto.response.UiTranslationDTO;
import org.test.restaurant_service.entity.Language;
import org.test.restaurant_service.service.LanguageService;
import org.test.restaurant_service.service.ProductService;
import org.test.restaurant_service.service.ProductTypeService;
import org.test.restaurant_service.service.impl.cache.AvailableLanguagesCacheService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AvailableLanguageService {

    private final ProductService productService;
    private final ProductI18nService productI18nService;
    private final AvailableLanguagesCacheService availableLanguagesCacheService;
    private final ProductTypeI18nService productTypeI18nService;
    private final LanguageService languageService;
    private final ProductTypeService typeService;
    private final UiTranslationService uiTranslationService;

    public AvailableLanguageService(@Qualifier("productServiceWithS3Impl") ProductService productService, ProductI18nService productI18nService, AvailableLanguagesCacheService availableLanguagesCacheService, ProductTypeI18nService productTypeI18nService, LanguageService languageService, ProductTypeServiceImpl typeService, UiTranslationService uiTranslationService) {
        this.productService = productService;
        this.productI18nService = productI18nService;
        this.availableLanguagesCacheService = availableLanguagesCacheService;
        this.productTypeI18nService = productTypeI18nService;
        this.languageService = languageService;
        this.typeService = typeService;
        this.uiTranslationService = uiTranslationService;
    }


    @Scheduled(cron = "0 0 5 * * *")
    public void checkAvailableLanguages() {
        List<Language> allLangs = languageService.getAll();
        Map<Language, Boolean> langs = new HashMap<>();

        List<ProductIdsResponse> allProductsId = productService.getAllProductsId();
        List<ProductTypeResponseDTO> types = typeService.getAll();

        for (Language language : allLangs) {
            boolean existTranslates = true;

            if (!"ru".equals(language.getCode())) {
                for (ProductIdsResponse productId : allProductsId) {
                    boolean hasProductTrans = productI18nService.existTranslation(productId.getId(), language.getId());
                    if (!hasProductTrans) {
                        existTranslates = false;
                        break;
                    }
                }
                if (!existTranslates) {
                    langs.put(language, false);
                    continue;
                }

                for (ProductTypeResponseDTO type : types) {
                    boolean hasTypeTrans = productTypeI18nService.existsTranslation(type.getId(), language.getId());
                    if (!hasTypeTrans) {
                        existTranslates = false;
                        break;
                    }
                }
                if (!existTranslates) {
                    langs.put(language, false);
                    continue;
                }

                List<UiTranslationDTO> uiTranslations = uiTranslationService.getAllByLangId(language.getId());
                for (UiTranslationDTO uiDto : uiTranslations) {
                    boolean hasUiTrans = uiTranslationService.existByKeyAndLanguage(uiDto.getKey(), language);
                    if (!hasUiTrans) {
                        existTranslates = false;
                        break;
                    }
                }
                if (!existTranslates) {
                    langs.put(language, false);
                    continue;
                }

                langs.put(language, true);
            } else {
                langs.put(language, true);
            }
        }

        for (Map.Entry<Language, Boolean> entry : langs.entrySet()) {
            Language lang = entry.getKey();
            Boolean isAvailable = entry.getValue();

            if (Boolean.TRUE.equals(isAvailable)) {
                availableLanguagesCacheService.saveLanguageToAvailable(lang);
            }
        }
    }
}
