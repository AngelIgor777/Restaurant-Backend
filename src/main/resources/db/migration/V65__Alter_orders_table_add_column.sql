ALTER TABLE restaurant_service.orders
    ADD COLUMN IF NOT EXISTS otp VARCHAR(3);