package org.test.restaurant_service.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.response.ProductIdsResponse;
import org.test.restaurant_service.dto.response.ProductTypeResponseDTO;
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

    public AvailableLanguageService(@Qualifier("productServiceWithS3Impl") ProductService productService, ProductI18nService productI18nService, AvailableLanguagesCacheService availableLanguagesCacheService, ProductTypeI18nService productTypeI18nService, LanguageService languageService, ProductTypeServiceImpl typeService) {
        this.productService = productService;
        this.productI18nService = productI18nService;
        this.availableLanguagesCacheService = availableLanguagesCacheService;
        this.productTypeI18nService = productTypeI18nService;
        this.languageService = languageService;
        this.typeService = typeService;
    }


    @Scheduled(cron = "0 0 5 * * *")
    public void checkAvailableLanguages() {

        List<Language> all = languageService.getAll();
        HashMap<Language, Boolean> langs = new HashMap<>();

        List<ProductIdsResponse> allProductsId = productService.getAllProductsId();
        List<ProductTypeResponseDTO> types = typeService.getAll();
        for (Language language : all) {
            boolean existTranslates = true;

            for (ProductIdsResponse productIdsResponse : allProductsId) {

                if (!productI18nService.existTranslation(productIdsResponse.getId(), language.getId())) {
                    existTranslates = false;
                    langs.put(language, false);
                    break;
                }
            }

            if (!existTranslates) {
                break;
            }

            //check for types
            for (ProductTypeResponseDTO type : types) {
                if (!productTypeI18nService.existsTranslation(type.getId(), language.getId())) {
                    existTranslates = false;
                    langs.put(language, false);
                    break;
                }
            }

            if (!existTranslates) {
                break;
            }

            langs.put(language, true);
        }

        for (Map.Entry<Language, Boolean> languageBooleanEntry : langs.entrySet()) {
            Language key = languageBooleanEntry.getKey();
            Boolean available = langs.put(key, languageBooleanEntry.getValue());

            if (Boolean.TRUE.equals(available) || key.getCode().equals("ru")) {
                availableLanguagesCacheService.saveLanguageToAvailable(key);
            }
        }


    }
}
