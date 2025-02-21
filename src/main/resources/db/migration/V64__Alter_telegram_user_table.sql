ALTER TABLE restaurant_service.telegram_user
    ADD COLUMN language_id INT REFERENCES restaurant_service.language(id) DEFAULT 1;