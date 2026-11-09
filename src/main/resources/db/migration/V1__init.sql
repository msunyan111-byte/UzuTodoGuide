CREATE TABLE projects (
    id                UUID PRIMARY KEY,
    title             VARCHAR(200)  NOT NULL,
    furigana          VARCHAR(200),
    genre             VARCHAR(100),
    player_count      INTEGER,
    estimated_minutes INTEGER,
    release_type      VARCHAR(20)   NOT NULL,
    stage             VARCHAR(30)   NOT NULL,
    studio_work_id    VARCHAR(200),
    studio_url        VARCHAR(1000),
    public_url        VARCHAR(1000),
    memo              TEXT,
    archived          BOOLEAN       NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP     NOT NULL,
    updated_at        TIMESTAMP     NOT NULL
);

CREATE INDEX idx_projects_archived ON projects (archived);
CREATE INDEX idx_projects_stage ON projects (stage);

CREATE TABLE checklist_items (
    id            UUID PRIMARY KEY,
    project_id    UUID          NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    name          VARCHAR(200)  NOT NULL,
    description   TEXT,
    status        VARCHAR(20)   NOT NULL,
    memo          TEXT,
    due_date      DATE,
    display_order INTEGER       NOT NULL,
    created_at    TIMESTAMP     NOT NULL,
    updated_at    TIMESTAMP     NOT NULL
);

CREATE INDEX idx_checklist_items_project ON checklist_items (project_id, display_order);
