ALTER TABLE fish_time.users ADD FOREIGN KEY (oauth_provider) REFERENCES fish_time.oauth_providers (id);

ALTER TABLE fish_time.users_map_roles ADD FOREIGN KEY (user_id) REFERENCES fish_time.users (id);

ALTER TABLE fish_time.users_map_roles ADD FOREIGN KEY (role_id) REFERENCES fish_time.roles (id);

ALTER TABLE fish_time.meetings ADD FOREIGN KEY (room_id) REFERENCES fish_time.rooms (id);

ALTER TABLE fish_time.meetings ADD FOREIGN KEY (created_by) REFERENCES fish_time.users (id);

ALTER TABLE fish_time.participants ADD FOREIGN KEY (user_id) REFERENCES fish_time.users (id);

ALTER TABLE fish_time.participants ADD FOREIGN KEY (meeting_id) REFERENCES fish_time.meetings (id);
