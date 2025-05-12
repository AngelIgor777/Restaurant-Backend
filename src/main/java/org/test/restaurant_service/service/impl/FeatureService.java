package org.test.restaurant_service.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.test.restaurant_service.dto.feats.Features;
import org.test.restaurant_service.dto.response.FeatureStatusResponseDTO;
import org.test.restaurant_service.service.impl.cache.FeatureCacheService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Бизнес-логика работы с фичами: включение, выключение и получение статусов.
 */
@Service
@RequiredArgsConstructor
public class FeatureService {
    private final FeatureCacheService cacheService;

    /**
     * Включает фичу навсегда.
     */
    public void enableFeature(Features feature) {
        cacheService.saveFeature(feature);
    }

    /**
     * Отключает фичу.
     */
    public void disableFeature(Features feature) {
        cacheService.deleteFeature(feature);

    }


    public FeatureStatusResponseDTO getFeatureStatus(Features feature) {
        boolean enabled = cacheService.getFeature(feature) != null;
        return new FeatureStatusResponseDTO(feature, enabled);
    }

    /**
     * Возвращает список всех фичей с их статусами.
     */
    public List<FeatureStatusResponseDTO> getAllFeaturesStatus() {
        return Arrays.stream(Features.values())
                .map(f -> new FeatureStatusResponseDTO(f, cacheService.getFeature(f) != null))
                .collect(Collectors.toList());
    }
}
