ALTER TABLE users
    ALTER COLUMN code_expires_at TYPE TIMESTAMP USING code_expires_at::timestamp;

ALTER TABLE users
   add COLUMN verification_code VARCHAR(10) NOT NULL;

ALTER TABLE users ALTER COLUMN is_active SET NOT NULL;

SELECT * FROM subscriptions;

select * from users;

delete from users where id = 'cff2ab7f-c88c-4d74-bc74-1d3e6d0c0a60';