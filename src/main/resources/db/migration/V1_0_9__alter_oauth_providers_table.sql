ALTER TABLE fish_time.oauth_providers DROP COLUMN url;
ALTER TABLE fish_time.oauth_providers ADD COLUMN authorization_url varchar;
ALTER TABLE fish_time.oauth_providers ADD COLUMN token_url varchar;