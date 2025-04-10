ALTER TABLE restaurant_service.product_history
    DROP CONSTRAINT product_history_type_id_fkey;

ALTER TABLE restaurant_service.product_history
    ADD CONSTRAINT product_history_type_id_fkey
        FOREIGN KEY (type_id)
            REFERENCES restaurant_service.product_types(id)
            ON DELETE CASCADE;

