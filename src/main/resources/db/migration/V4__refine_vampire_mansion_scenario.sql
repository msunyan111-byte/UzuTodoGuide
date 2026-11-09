-- V3で登録したサンプルシナリオの設計矛盾を解消するためのデータ更新。
UPDATE projects
SET memo = 'ユーザー提供のシナリオ設計メモ（2026-11-03）。確定: 被害者なしの殺人未遂（会場のニンニク装飾）。現場はパーティー会場、犯行は17:00の施錠前。ランドルフは吸血鬼ハンターで、十字架襲撃に失敗した部下の報告を受け、ニンニクに切り替えた。ルーシィは被害者ではなく、ファフルが救った元人間の使い魔。ルーシィはニンニクに不快感はあるが触れられる（眷属元が高位吸血鬼）。ルーシィの復讐対象は吸血鬼社会全体で、特定の個人ではない。得点ルールはプロジェクト設定で選択可能（同数最多も可/単独最多のみ）。今回は同数最多も可。未確定: 全カード最終文面、UZU上の設定、シミュレーション条件。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c';

UPDATE checklist_items
SET description = '吸血鬼、使い魔、長老、ニンニク・十字架・銀への耐性、吸血鬼社会のルールを統一する。高位吸血鬼は耐性を持ち、使い魔は主人の耐性を部分的に受け継ぐ。ニンニク料理は高位吸血鬼向けの珍味として振る舞われることがある。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000202';

UPDATE checklist_items
SET description = 'ギャリー、ランドルフ、ルーシィ、ファフルの表向き設定・秘密・目的・得点・プレイ方針を確定する。ルーシィの復讐対象は特定の個人ではなく吸血鬼社会全体であり、ランドルフはその社会の裁定を混乱させるための駒として利用する。ファフルは瀕死のルーシィを使い魔にして救ったが、彼も吸血鬼社会の一部である。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000203';

UPDATE checklist_items
SET description = '被害者なしの殺人未遂。ランドルフが会場にニンニクを飾り、次期長老候補と参加者を弱体化・殺害しようとした事件として扱う。ルーシィは被害者ではなくプレイヤー。',
    memo = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000204';

UPDATE checklist_items
SET description = 'A-02を会場カードに書き直す。17:00に主催者が施錠し、鍵は主催者が保管。17:30に最終確認のため解錠したところ、会場中央に大量のニンニクが飾られていた。室内に荒らされた形跡はない。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000205';

UPDATE checklist_items
SET description = '犯行は16:30〜17:00の自由出入り時間。ランドルフは16:15に厨房から20袋を持ち出し、16:30〜17:00に会場を飾り、17:00の施錠前に退室した。施錠は発見を遅らせただけ。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000206';

UPDATE checklist_items
SET description = 'ランドルフは吸血鬼ハンター。十字架襲撃は部下に実行させ、15:00に失敗の報告を受けてニンニクに切り替えた。銀製カトラリーは商人の見本兼武器。B-02、B-01、A-01の付き人退去記録と整合させる。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000207';

UPDATE checklist_items
SET description = '前夜ランドルフ入館、14:00ファフルが30袋を厨房へ、15:00十字架襲撃失敗と部下退去、16:00ファフルがトイレへ、16:00準備スタッフ退室、16:15ランドルフが20袋を持ち出す、16:30〜17:00飾り付け、17:00施錠、17:30解錠・発覚。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000208';

UPDATE checklist_items
SET description = 'ギャリーは16:00〜17:00ラウンジ、ルーシィは16:00〜17:00ファン対応、ランドルフは15:00庭園で指示出しと5分単位の離席、ファフルは16:00〜17:00トイレ（裏口あり）。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000209';

UPDATE checklist_items
SET description = 'A-01〜A-10、B-01〜B-11、C-01、E-01〜E-06の全18枚を最終化する。A-02は会場カード、A-01は付き人退去記録、A-03は銀の装飾紐、A-08はスタッフ退室、A-09はトイレの裏口、B-11は現在の候補者名を書かない記録、E-06はファフルとルーシィの過去、およびルーシィの家族が吸血鬼社会の争いで失われたことを示す記述に修正する。',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000210';

UPDATE checklist_items
SET description = '強制公開①「会場の施錠」、強制公開②「ニンニクの消費とハンターの影」の2枚に統一する。①で施錠と鍵の保管、②でファフルの10袋消費と20袋残存、ハンターの手口を公開する。',
    memo = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000211';

UPDATE checklist_items
SET description = '通常18枚は全公開。序盤7枚（A-01, A-02, A-03, B-02, B-04, A-06, C-01）、中盤6枚（A-05, A-08, B-01, A-07, A-09, A-10）、終盤5枚（B-10, B-11, E-01, E-04, E-06）。強制①は終盤前半、強制②は終盤最後。',
    memo = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000212';

UPDATE checklist_items
SET description = '得点ルールの「最多にならない」判定はプロジェクト設定 scoringRule で選択できるようにする。選択肢は TIE_ALLOWED（同数最多も可）と SOLE_TOP_REQUIRED（単独最多のみ）。今回のテストシナリオは TIE_ALLOWED。各投票は1人1票、棄権なし。',
    memo = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000213';

UPDATE checklist_items
SET description = 'ルーシィがランドルフに投票（4点）。ルーシィが使い魔投票で単独最多にならない（3点）。密かな復讐: ランドルフへの投票がルーシィの1票のみで、かつランドルフが単独最多でない（同数最多は可）（3点）。これは吸血鬼社会の裁定を誤らせ、人間の犯人を逃がす（または社会の矛盾を暴く）ための妨害であり、個人への復讐ではない。ルーシィが犯人投票で単独最多になると密かな復讐は失敗。',
    memo = NULL,
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000214';

UPDATE checklist_items
SET description = '全公開18枚、強制2枚、1人1票・棄権なし、プロジェクト設定の得点ルール（今回は TIE_ALLOWED）、得点条件を固定して1000回以上試行する。',
    memo = '未確定',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000216';

UPDATE checklist_items
SET description = '全カードの最終文面、UZU上のキャラクター・カード・フェーズ・投票設定、シミュレーション結果の反映を確定する。',
    memo = '未確定',
    updated_at = CURRENT_TIMESTAMP
WHERE id = '7f1c2b3a-4d5e-4f6a-8b9c-000000000217';
