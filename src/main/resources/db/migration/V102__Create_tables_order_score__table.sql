CREATE TABLE IF NOT EXISTS restaurant_service.tables_order_score
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    table_id     INTEGER   NOT NULL REFERENCES restaurant_service.tables (id) ON DELETE CASCADE,
    order_id     INTEGER   NOT NULL REFERENCES restaurant_service.orders (id) ON DELETE CASCADE,
    session_uuid UUID      NOT NULL,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (table_id, order_id)
);