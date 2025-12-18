-- Make refresh_token and refresh_token_expiry nullable
ALTER TABLE users
    ALTER COLUMN "refresh_token" DROP NOT NULL;

ALTER TABLE users
    ALTER COLUMN refreshtoken DROP NOT NULL;


ALTER TABLE users
    ALTER COLUMN "refresh_token_expiry" DROP NOT NULL;


select * from subscriptions;

select *  from users;

delete from subscriptions where id = '79430a37-7f87-4abc-8234-0058a5b5a661';