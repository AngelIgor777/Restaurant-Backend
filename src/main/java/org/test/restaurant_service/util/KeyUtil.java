package org.test.restaurant_service.util;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import org.springframework.stereotype.Component;


@Component
public class KeyUtil {

    @Getter
    private static String accessSecret = "";

    @Getter
    private static String refreshSecret = "";

    public static void setProperties(Dotenv dotenv) {
        // Прокидываем переменные в System Environment
        dotenv.entries().forEach(entry -> {
            String value = entry.getValue();
            System.setProperty(entry.getKey(), value);


            if (entry.getKey().equals("JWT_ACCESS")) {
                accessSecret = value;
            }

            if (entry.getKey().equals("JWT_REFRESH")) {
                refreshSecret = value;
            }
        });
    }

}