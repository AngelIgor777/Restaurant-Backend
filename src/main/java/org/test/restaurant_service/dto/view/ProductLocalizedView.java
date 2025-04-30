package org.test.restaurant_service.dto.view;

import java.math.BigDecimal;
import java.time.LocalTime;

public interface ProductLocalizedView {

    Integer   getId();
    String    getName();          // локализовано
    String    getDescription();   // локализовано
    String    getTypeName();      // локализовано
    BigDecimal getPrice();
    LocalTime  getCookingTime();
    String    getPhotoUrl();      // главная фотка, если нужна
}