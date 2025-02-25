ALTER TABLE fish_time.meetings ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.oauth_providers ADD COLUMN created_time timestamptz NOT NULL;
ALTER TABLE fish_time.oauth_providers ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.oauth_providers ADD COLUMN modified_time timestamptz;
ALTER TABLE fish_time.oauth_providers ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.participants ADD COLUMN created_time timestamptz NOT NULL;
ALTER TABLE fish_time.participants ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.participants ADD COLUMN modified_time timestamptz;
ALTER TABLE fish_time.participants ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.roles ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.roles ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.rooms ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.rooms ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.users ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.users ADD COLUMN modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.users_map_roles ADD COLUMN created_by integer references fish_time.users(id);
ALTER TABLE fish_time.users_map_roles ADD COLUMN modified_time timestamptz;
ALTER TABLE fish_time.users_map_roles ADD COLUMN modified_by integer references fish_time.users(id);