ALTER TABLE restaurant_service.order_discount
    DROP CONSTRAINT IF EXISTS order_discount_order_id_fkey;

ALTER TABLE restaurant_service.order_discount
    ADD CONSTRAINT order_discount_order_id_fkey
        FOREIGN KEY (order_id) REFERENCES restaurant_service.orders (id) ON DELETE CASCADE;
