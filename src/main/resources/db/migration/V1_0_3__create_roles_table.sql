CREATE TABLE IF NOT EXISTS fish_time.roles (
    id SERIAL PRIMARY KEY,
    name varchar NOT NULL,
    description varchar,
    created_time timestamptz NOT NULL DEFAULT (now()),
    modified_time timestamptz
);

create sequence if not exists fish_time.roles_seq increment by 1 start with 1;