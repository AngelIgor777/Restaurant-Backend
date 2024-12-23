CREATE TABLE IF NOT EXISTS restaurant_service.user_roles
(
    user_id INTEGER REFERENCES restaurant_service.users (id) ON DELETE CASCADE,
    role_id INTEGER REFERENCES restaurant_service.roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

