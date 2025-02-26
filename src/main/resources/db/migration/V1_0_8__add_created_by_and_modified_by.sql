ALTER TABLE fish_time.meetings ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS created_time timestamptz NOT NULL;
ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS modified_time timestamptz;
ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.participants ADD COLUMN IF NOT EXISTS created_time timestamptz NOT NULL;
ALTER TABLE fish_time.participants ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.participants ADD COLUMN IF NOT EXISTS modified_time timestamptz;
ALTER TABLE fish_time.participants ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.roles ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.roles ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.rooms ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.rooms ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.users ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.users ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);

ALTER TABLE fish_time.users_map_roles ADD COLUMN IF NOT EXISTS created_by integer references fish_time.users(id);
ALTER TABLE fish_time.users_map_roles ADD COLUMN IF NOT EXISTS modified_time timestamptz;
ALTER TABLE fish_time.users_map_roles ADD COLUMN IF NOT EXISTS modified_by integer references fish_time.users(id);