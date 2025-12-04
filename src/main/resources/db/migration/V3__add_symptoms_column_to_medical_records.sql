ALTER TABLE medical_records
    ADD COLUMN IF NOT EXISTS symptoms TEXT;

-- Backfill symptoms from related appointment notes if missing
UPDATE medical_records mr
SET symptoms = ap.symptoms
FROM appointments ap
WHERE mr.appointment_id = ap.id
  AND mr.symptoms IS NULL;

-- Refresh tsvector columns for existing records
UPDATE medical_records
SET symptoms_tsvector = to_tsvector('english', COALESCE(symptoms, '')),
    diagnosis_tsvector = to_tsvector('english', COALESCE(diagnosis, ''));
