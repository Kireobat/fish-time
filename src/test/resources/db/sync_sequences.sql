SELECT setval('fish_time.meetings_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.meetings), 0) + 1);
SELECT setval('fish_time.meetings_seq', COALESCE((SELECT MAX(id) FROM fish_time.meetings), 0) + 1);

SELECT setval('fish_time.oauth_providers_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.oauth_providers), 0) + 1);
SELECT setval('fish_time.oauth_providers_seq', COALESCE((SELECT MAX(id) FROM fish_time.oauth_providers), 0) + 1);

SELECT setval('fish_time.participants_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.participants), 0) + 1);
SELECT setval('fish_time.participants_seq', COALESCE((SELECT MAX(id) FROM fish_time.participants), 0) + 1);

SELECT setval('fish_time.roles_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.roles), 0) + 1);
SELECT setval('fish_time.roles_seq', COALESCE((SELECT MAX(id) FROM fish_time.roles), 0) + 1);

SELECT setval('fish_time.rooms_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.roles), 0) + 1);
SELECT setval('fish_time.rooms_seq', COALESCE((SELECT MAX(id) FROM fish_time.roles), 0) + 1);

SELECT setval('fish_time.users_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.users), 0) + 1);
SELECT setval('fish_time.users_seq', COALESCE((SELECT MAX(id) FROM fish_time.users), 0) + 1);

SELECT setval('fish_time.users_map_roles_id_seq', COALESCE((SELECT MAX(id) FROM fish_time.users_map_roles), 0) + 1);
SELECT setval('fish_time.users_map_roles_seq', COALESCE((SELECT MAX(id) FROM fish_time.users_map_roles), 0) + 1);
