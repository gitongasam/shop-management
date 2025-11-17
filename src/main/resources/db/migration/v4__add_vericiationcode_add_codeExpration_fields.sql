ALTER TABLE users
    ALTER COLUMN code_expires_at TYPE TIMESTAMP USING code_expires_at::timestamp;

