INSERT INTO restaurant_service.roles
    (name)
VALUES ('ROLE_COOK')
ON CONFLICT (name) DO NOTHING;
