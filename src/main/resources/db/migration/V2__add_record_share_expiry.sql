-- Add expiry_date column to record_shares for time-bounded sharing
ALTER TABLE record_shares
    ADD COLUMN IF NOT EXISTS expiry_date TIMESTAMP NULL;

CREATE INDEX IF NOT EXISTS idx_shares_expiry ON record_shares(expiry_date);

