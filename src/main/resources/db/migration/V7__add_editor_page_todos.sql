-- サンプルシナリオのTODOを、UZU Studio の左タブ（編集ページ）単位に補完する。
-- キャラクター配下のNPCと、演出配下のBGM・SE・ムービー・背景画像・カラーテーマを追加する。
UPDATE checklist_items
SET display_order = display_order + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND display_order >= 9;

UPDATE checklist_items
SET display_order = display_order + 4,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND display_order >= 29;

DELETE FROM checklist_items
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【演出】BGM・SE・画面効果を設定する';

INSERT INTO checklist_items (
    id, project_id, name, description, status, memo, due_date, display_order, created_at, updated_at
) VALUES
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000347', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【NPC】NPCと表示方法を登録する',
     '「NPC」を開き、プレイヤーが演じない登場人物を登録します。名前、ふりがな、説明、アイコン、登場人物一覧への表示、セリフの話者を設定します。完了条件: 必要なNPCが登録され、秘密NPCの情報が一覧に表示されていない。',
     'NOT_STARTED', NULL, NULL, 9, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000348', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【BGM】再生タイミングとループを設定する',
     '「BGM」を開き、フェーズや場面ごとのBGM、再生タイミング、停止、音量、ループ位置を設定します。最初のフェーズの自動再生はブラウザで鳴らない場合があります。完了条件: 音量差が大きすぎず、再生タイミングとループが意図どおり。',
     'NOT_STARTED', NULL, NULL, 28, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000349', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【SE】効果音とナレーションを設定する',
     '「SE」を開き、効果音とナレーションの音源、再生タイミング、音量、連打時の扱いを設定します。完了条件: 効果音が重なっても聞き取れ、ナレーションとBGMの音量バランスが取れている。',
     'NOT_STARTED', NULL, NULL, 29, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000350', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【ムービー】動画とスキップ設定を確認する',
     '「ムービー」を開き、オープニングや場面転換で再生する動画、再生タイミング、スキップ可否、終了後の遷移を設定します。動画はMP4で10MBまでです。完了条件: 動画が最後まで再生され、スキップ時もプレイが止まらない。',
     'NOT_STARTED', NULL, NULL, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000351', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【背景画像】デフォルト背景と条件付き背景を設定する',
     '「背景画像」を開き、デフォルト背景と条件付き背景、表示条件を設定します。縦長（9:16または3:4）を推奨し、背景のぼかしはOFFにできない点に注意します。完了条件: スマホとPCでトリミングが崩れず、背景の切り替え条件が意図どおり。',
     'NOT_STARTED', NULL, NULL, 31, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000352', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【カラーテーマ】色テーマと適用タイミングを設定する',
     '「カラーテーマ」を開き、基本色、アクセント色、適用タイミングを設定します。設定したタイミングで色が変わらない場合があるため、プレビューで確認します。完了条件: 明るい画面と暗い画面の両方で文字が読め、テーマが想定どおりに切り替わる。',
     'NOT_STARTED', NULL, NULL, 32, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
