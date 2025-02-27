ALTER TABLE restaurant_service.telegram_user
    ADD COLUMN IF NOT EXISTS language_id INT REFERENCES restaurant_service.language (id) DEFAULT 1;
ALTER TABLE restaurant_service.telegram_user
    ALTER COLUMN username DROP NOT NULL;