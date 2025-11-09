-- Make refresh_token and refresh_token_expiry nullable
ALTER TABLE users
    ALTER COLUMN "refresh_token" DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN "refresh_token_expiry" DROP NOT NULL;
