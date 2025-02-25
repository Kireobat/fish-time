CREATE TABLE IF NOT EXISTS fish_time.users (
    id SERIAL PRIMARY KEY,
    username varchar NOT NULL,
    password_hash varchar,
    email varchar(255),
    oauth_provider integer,
    oauth_id integer,
    created_time timestamptz NOT NULL,
    modified_time timestamptz
);

create sequence if not exists fish_time.users_seq increment by 1 start with 10;