-- v3_change_email_to_location.sql
-- Remove 'email' column and add new 'location' column

ALTER TABLE customers
    DROP COLUMN email,
    ADD COLUMN location VARCHAR(150);



ALTER TABLE customers ADD COLUMN location VARCHAR(150);

UPDATE customers SET location = 'Unknown' WHERE location IS NULL;


