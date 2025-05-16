ALTER TABLE restaurant_service.orders
    DROP CONSTRAINT IF EXISTS orders_table_id_fkey;

ALTER TABLE restaurant_service.orders
    ADD CONSTRAINT orders_table_id_fkey
        FOREIGN KEY (table_id)
            REFERENCES restaurant_service.tables(id)
            ON DELETE SET NULL;