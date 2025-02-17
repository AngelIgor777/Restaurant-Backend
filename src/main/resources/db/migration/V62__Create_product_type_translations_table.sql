CREATE TABLE IF NOT EXISTS restaurant_service.product_type_translations
(
    id              SERIAL PRIMARY KEY,
    product_type_id INTEGER      NOT NULL REFERENCES restaurant_service.product_types (id) ON DELETE CASCADE,
    language_code   VARCHAR(10)  NOT NULL,
    name            VARCHAR(128) NOT NULL,
    UNIQUE (product_type_id, language_code)
);
