CREATE TABLE IF NOT EXISTS fish_time.rooms (
    id SERIAL PRIMARY KEY,
    name varchar NOT NULL,
    capacity integer NOT NULL,
    address varchar NOT NULL,
    active bool NOT NULL DEFAULT TRUE,
    created_time timestamptz NOT NULL,
    modified_time timestamptz
);

create sequence if not exists fish_time.rooms_seq increment by 1 start with 10;