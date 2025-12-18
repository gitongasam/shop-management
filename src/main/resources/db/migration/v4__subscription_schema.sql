CREATE TABLE subscriptions(
                              id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                              user_id UUID REFERENCES users(id) ON DELETE CASCADE,
                              plan VARCHAR(100) NOT NULL,
                              start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              end_date TIMESTAMP,
                              status VARCHAR(50) DEFAULT 'INACTIVE',
                              created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                              updated_at TIMESTAMP,
                              deleted_at TIMESTAMP
);

ALTER TABLE users ADD COLUMN phone_number VARCHAR(20);



select * from users;

select * from subscriptions;

delete from users where id = 'f9b9d970-4f61-4c3c-8a4b-d98b7e5443d4';