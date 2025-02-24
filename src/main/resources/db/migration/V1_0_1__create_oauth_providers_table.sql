CREATE TABLE IF NOT EXISTS fish_time.oauth_providers (
   id SERIAL PRIMARY KEY,
   name varchar NOT NULL,
   url varchar NOT NULL
);

create sequence if not exists fish_time.oauth_providers_seq increment by 1 start with 1;