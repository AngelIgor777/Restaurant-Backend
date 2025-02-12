package org.test.restaurant_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.test.restaurant_service.util.KeyUtil;

@SpringBootApplication
@EnableCaching
@EnableAsync
public class RestaurantServiceApplication {

	public static void main(String[] args) {
		// Инициализация property из .env
		Dotenv dotenv = Dotenv.configure().load();

		KeyUtil.setProperties(dotenv);

		SpringApplication.run(RestaurantServiceApplication.class, args);
	}

}
