-- Add department reference to appointments to align with entity mapping
ALTER TABLE appointments
    ADD COLUMN department_id BIGINT REFERENCES departments(id);

-- Add queue number support for appointments
ALTER TABLE appointments
    ADD COLUMN queue_number INTEGER;

-- Index department for faster filtering
CREATE INDEX idx_appointments_department ON appointments(department_id);
