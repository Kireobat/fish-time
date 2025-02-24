CREATE TABLE IF NOT EXISTS fish_time.meetings (
    id SERIAL PRIMARY KEY,
    title varchar NOT NULL,
    description varchar,
    room_id integer,
    created_by integer,
    start_time timestamptz NOT NULL,
    end_time timestamptz NOT NULL,
    created_time timestamptz NOT NULL DEFAULT (now()),
    modified_time timestamptz
);

create sequence if not exists fish_time.meetings_seq increment by 1 start with 1;