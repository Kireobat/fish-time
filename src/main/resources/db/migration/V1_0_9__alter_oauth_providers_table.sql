ALTER TABLE fish_time.oauth_providers DROP COLUMN IF EXISTS url;
ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS authorization_url varchar;
ALTER TABLE fish_time.oauth_providers ADD COLUMN IF NOT EXISTS token_url varchar;