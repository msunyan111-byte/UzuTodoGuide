-- チェックリスト項目に画面セクションを保持し、セクション単位のメモと最終確認日を保存する。
ALTER TABLE checklist_items ADD COLUMN section_key VARCHAR(50);

UPDATE checklist_items AS item
SET section_key = mapping.section_key
FROM (
    VALUES
        ('作品登録', 'CREATE'),
        ('作品概要', 'SUMMARY'),
        ('サブ画像', 'SUB_IMAGES'),
        ('ジャンル', 'GENRE'),
        ('マーケティング', 'MARKETING'),
        ('追加コンテンツ', 'ADDITIONAL'),
        ('無料プレイチケット', 'TICKET'),
        ('リリース管理', 'RELEASE'),
        ('レポート', 'REPORT'),
        ('メンバー', 'MEMBERS'),
        ('エディション', 'EDITION'),
        ('キャラクター', 'CHARACTERS'),
        ('NPC', 'NPC'),
        ('フェーズ', 'PHASES'),
        ('フェーズ進行', 'FLOW'),
        ('テキスト', 'TEXTS'),
        ('手がかり', 'CLUES'),
        ('山札', 'DECK'),
        ('ルーム', 'ROOMS'),
        ('トークン', 'TOKENS'),
        ('自由記述', 'TEXT_FIELDS'),
        ('トリガー', 'TRIGGERS'),
        ('アクション', 'ACTIONS'),
        ('BGM', 'BGM'),
        ('SE', 'SE'),
        ('ムービー', 'MOVIE'),
        ('背景画像', 'BACKGROUND'),
        ('カラーテーマ', 'COLOR_THEME'),
        ('条件', 'CONDITIONS'),
        ('画面デザイン', 'SCREEN_DESIGN'),
        ('動作確認', 'DEMO'),
        ('バージョン', 'VERSION'),
        ('テストプレイ', 'PLAYTEST'),
        ('審査・公開', 'REVIEW'),
        ('外国語版', 'LOCALIZATION'),
        ('感想戦', 'REFLECTION'),
        ('発展テクニック', 'ADVANCED'),
        ('素材', 'ASSETS'),
        ('バージョン移行', 'MIGRATION'),
        ('作家プロフィール', 'PROFILE'),
        ('規約同意', 'TERMS'),
        ('売上・振込', 'REVENUE'),
        ('評価・バッジ', 'BADGES'),
        ('コレクション', 'COLLECTION')
) AS mapping(label, section_key)
WHERE item.name LIKE '【' || mapping.label || '】%';

UPDATE checklist_items
SET section_key = 'OTHER'
WHERE section_key IS NULL;

ALTER TABLE checklist_items ALTER COLUMN section_key SET NOT NULL;

CREATE INDEX idx_checklist_items_project_section ON checklist_items (project_id, section_key);

CREATE TABLE project_section_states (
    id           UUID PRIMARY KEY,
    project_id   UUID         NOT NULL REFERENCES projects (id) ON DELETE CASCADE,
    section_key  VARCHAR(50)  NOT NULL,
    memo         TEXT,
    verified_at  TIMESTAMP,
    created_at   TIMESTAMP    NOT NULL,
    updated_at   TIMESTAMP    NOT NULL,
    CONSTRAINT uq_project_section_states UNIQUE (project_id, section_key)
);

CREATE INDEX idx_project_section_states_project ON project_section_states (project_id);
