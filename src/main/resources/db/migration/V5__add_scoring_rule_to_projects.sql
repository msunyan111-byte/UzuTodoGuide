ALTER TABLE projects
    ADD COLUMN scoring_rule VARCHAR(30) NOT NULL DEFAULT 'TIE_ALLOWED';

UPDATE projects
SET scoring_rule = 'TIE_ALLOWED'
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c';
