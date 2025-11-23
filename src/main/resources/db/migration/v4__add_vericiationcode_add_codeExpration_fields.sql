ALTER TABLE users
    ALTER COLUMN code_expires_at TYPE TIMESTAMP USING code_expires_at::timestamp;

ALTER TABLE users
   add COLUMN IS_ACTIVE boolean DEFAULT FALSE;

ALTER TABLE users ALTER COLUMN is_active SET NOT NULL;

