CREATE TABLE IF NOT EXISTS restaurant_service.language
(
    id   SERIAL PRIMARY KEY,
    code VARCHAR(5)  NOT NULL UNIQUE,
    name VARCHAR(50) NOT NULL
);
INSERT INTO restaurant_service.language (code, name)
VALUES ('ru', 'Русский'),
       ('ro', 'Română');
