package org.test.restaurant_service.util;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class KeyUtil {

    public static void setProperties(Dotenv dotenv) {
        // Прокидываем переменные в System Environment
        dotenv.entries().forEach(entry -> {
            String value = entry.getValue();
            System.setProperty(entry.getKey(), value);
        });
    }

}