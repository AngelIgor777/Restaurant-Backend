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

    @Getter
    private static String bucketName;

    @Getter
    private static String accessKey;

    @Getter
    private static String secretAccessKey;

    @Getter
    private static String adminCode1;

    @Getter
    private static String adminCode2;

    @Getter
    private static String disposableAdminKey;


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

            if (entry.getKey().equals("AWS_ACCESS_KEY")) {
                accessKey = value;
            }
            if (entry.getKey().equals("AWS_SECRET_ACCESS_KEY")) {
                secretAccessKey = value;
            }
            if (entry.getKey().equals("AWS_BUCKET_NAME")) {
                bucketName = value;
            }
            if (entry.getKey().equals("ADMIN_CODE1")) {
                adminCode1 = value;
            }
            if (entry.getKey().equals("ADMIN_CODE2")) {
                adminCode2 = value;
            }
            if (entry.getKey().equals("DISPOSABLE_ADMIN_KEY")) {
                disposableAdminKey = value;
            }
        });
    }

}