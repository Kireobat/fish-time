CREATE TABLE IF NOT EXISTS fish_time.users_map_roles (
    id SERIAL PRIMARY KEY,
    user_id integer,
    role_id integer,
    created_time timestamptz NOT NULL DEFAULT (now())
);

create sequence if not exists fish_time.users_map_roles_seq increment by 1 start with 1;