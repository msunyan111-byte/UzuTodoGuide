CREATE TABLE guide_articles (
    id          UUID PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    category    VARCHAR(40)  NOT NULL,
    summary     VARCHAR(500),
    content     TEXT,
    source_url  VARCHAR(1000),
    checked_at  DATE,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP NOT NULL
);

CREATE INDEX idx_guide_articles_category ON guide_articles (category, updated_at DESC);

CREATE TABLE playtest_records (
    id                    UUID PRIMARY KEY,
    project_id            UUID NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    played_at             DATE NOT NULL,
    platform              VARCHAR(20) NOT NULL,
    participant_count     INTEGER,
    version_note          VARCHAR(200),
    questionnaire_collected BOOLEAN NOT NULL DEFAULT FALSE,
    questionnaire_url     VARCHAR(1000),
    findings              TEXT,
    memo                  TEXT,
    created_at            TIMESTAMP NOT NULL,
    updated_at            TIMESTAMP NOT NULL
);

CREATE INDEX idx_playtest_records_project ON playtest_records (project_id, played_at DESC);

CREATE TABLE review_records (
    id            UUID PRIMARY KEY,
    project_id    UUID NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    review_type   VARCHAR(20) NOT NULL,
    submitted_at  DATE,
    status        VARCHAR(30) NOT NULL,
    result        TEXT,
    memo          TEXT,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL
);

CREATE INDEX idx_review_records_project ON review_records (project_id, submitted_at DESC);

CREATE TABLE review_issues (
    id             UUID PRIMARY KEY,
    review_id      UUID NOT NULL REFERENCES review_records (id) ON DELETE CASCADE,
    content        TEXT NOT NULL,
    status         VARCHAR(20) NOT NULL,
    response_memo  TEXT,
    created_at     TIMESTAMP NOT NULL,
    updated_at     TIMESTAMP NOT NULL
);

CREATE INDEX idx_review_issues_review ON review_issues (review_id, created_at);
