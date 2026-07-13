ALTER TABLE fingerprint_store
    ADD COLUMN IF NOT EXISTS image_uri VARCHAR(512),
    ADD COLUMN IF NOT EXISTS image_bucket VARCHAR(64) DEFAULT 'queue-processing',
    ADD COLUMN IF NOT EXISTS image_object_key VARCHAR(256);

CREATE INDEX IF NOT EXISTS idx_fingerprint_store_image_uri ON fingerprint_store(image_uri);
