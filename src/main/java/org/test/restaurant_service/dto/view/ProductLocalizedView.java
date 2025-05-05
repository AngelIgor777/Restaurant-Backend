package org.test.restaurant_service.dto.view;

import java.math.BigDecimal;
import java.time.LocalTime;

public interface ProductLocalizedView {

    Integer getId();

    String getName();

    String getDescription();

    String getTypeName();

    BigDecimal getPrice();

    LocalTime getCookingTime();

    String getPhotoUrl();
}