package org.test.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.test.restaurant_service.entity.UiTranslation;
import org.test.restaurant_service.entity.Language;

import java.util.List;
import java.util.Optional;

public interface UiTranslationRepository extends JpaRepository<UiTranslation, Integer> {
    List<UiTranslation> findAllByLanguage(Language language);
    Optional<UiTranslation> findByKeyAndLanguage(String key, Language language);

    boolean existsByKeyAndLanguage(String key, Language language);
}
