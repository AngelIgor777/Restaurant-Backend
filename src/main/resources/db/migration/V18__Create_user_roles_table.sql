CREATE TABLE IF NOT EXISTS restaurant_service.user_roles
(
    user_uuid uuid REFERENCES restaurant_service.users (uuid) ON DELETE CASCADE,
    role_id   INTEGER REFERENCES restaurant_service.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_uuid, role_id)
);

