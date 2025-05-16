CREATE TABLE IF NOT EXISTS restaurant_service.working_hours
(
    id          SERIAL PRIMARY KEY,
    day_of_week SMALLINT NOT NULL CHECK (day_of_week BETWEEN 1 AND 7),
    open_time   TIME     NOT NULL,
    close_time  TIME     NOT NULL
);