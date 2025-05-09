CREATE TABLE IF NOT EXISTS restaurant_service.shared_bucket
(
    id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    session_uuid UUID                 DEFAULT gen_random_uuid() NOT NULL,
    user_uuid  UUID references restaurant_service.users (uuid),
    status     VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('ACTIVE', 'CLOSED')),
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE OR REPLACE FUNCTION update_modified_column()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_shared_bucket_timestamp
    BEFORE UPDATE
    ON restaurant_service.shared_bucket
    FOR EACH ROW
EXECUTE FUNCTION update_modified_column();

CREATE INDEX IF NOT EXISTS idx_shared_bucket_session_id ON restaurant_service.shared_bucket (session_uuid);
CREATE INDEX IF NOT EXISTS idx_shared_bucket_user_uuid ON restaurant_service.shared_bucket (user_uuid);

