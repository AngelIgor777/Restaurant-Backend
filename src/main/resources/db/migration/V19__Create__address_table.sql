CREATE TABLE IF NOT EXISTS restaurant_service.address
(
    city             VARCHAR(64),
    street           VARCHAR(128),
    home_number      VARCHAR(128),
    apartment_number VARCHAR(8),
    user_id          INTEGER REFERENCES restaurant_service.users (id) ON DELETE CASCADE
);

