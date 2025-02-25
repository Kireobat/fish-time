CREATE TABLE IF NOT EXISTS fish_time.participants (
    id SERIAL PRIMARY KEY,
    user_id integer,
    meeting_id integer,
    status varchar
);

create sequence if not exists fish_time.participants_seq increment by 1 start with 10;