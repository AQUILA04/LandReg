-- Executed after backfill when claim-check is fully adopted
ALTER TABLE fingerprint_store DROP COLUMN IF EXISTS fingerprint_image;
