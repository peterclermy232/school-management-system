-- Backs the new Grade.examDate field.
ALTER TABLE grades ADD COLUMN IF NOT EXISTS exam_date DATE;
