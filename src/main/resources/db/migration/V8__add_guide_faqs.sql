CREATE TABLE guide_faqs (
    id            UUID PRIMARY KEY,
    question      VARCHAR(300) NOT NULL,
    answer        TEXT NOT NULL,
    category      VARCHAR(40) NOT NULL,
    section_key   VARCHAR(40),
    source_url    VARCHAR(1000) NOT NULL,
    checked_at    DATE NOT NULL,
    display_order INTEGER NOT NULL,
    published     BOOLEAN NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP NOT NULL,
    updated_at    TIMESTAMP NOT NULL
);

CREATE INDEX idx_guide_faqs_list ON guide_faqs (published, display_order);

INSERT INTO guide_faqs (
    id, question, answer, category, section_key, source_url, checked_at,
    display_order, published, created_at, updated_at
) VALUES
(
    'a1f00000-0000-4000-8000-000000000001',
    'プレイアブルキャラクター（PC）とNPCは何人まで作成できますか？',
    'PCは8人まで、NPCは理論上、無限に作成できます。PCの上限に達したときは、プレイヤーが演じない人物をNPCとして追加する方法があります。NPCはキャラクターごとに、プレイ中の登場人物紹介に表示するかを設定できます。',
    'CHARACTER', 'CHARACTERS',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#character',
    DATE '2026-11-10', 10, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000002',
    '条件付きアイコンが思ったタイミングで切り替わりません。',
    '条件付きアイコンは上から順に優先されます。後から表示したいアイコンほど上に並べ替えてください。例として、フェーズ1でA、フェーズ2でB、フェーズ3でCを表示する場合は、上からC→B→Aの順にします。Aに「フェーズ1が開始した かつ フェーズ2が踏まれなかった」条件を追加する方法も有効です。並び順を変えても切り替わらない場合は、各アイコンの条件も確認してください。',
    'CHARACTER', 'CHARACTERS',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#character',
    DATE '2026-11-10', 20, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000003',
    '設定したミッションはプレイ中に表示されますか？',
    '表示されません。ミッションは感想戦画面でのみ表示され、テキストタブなどに自動で配布されることはありません。プレイ中にミッションを伝えたい場合は、配布するテキスト内に文字情報として記載してください。',
    'CHARACTER', 'REFLECTION',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#character',
    DATE '2026-11-10', 30, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000004',
    'フェーズ進行をすべて条件遷移にできません。',
    '仕様です。条件遷移だけにすると、どの条件にも当てはまらない場合に行き先がなくなり、進行不能になるため、必ず1本は基本遷移（黒い線）にします。基本遷移は「次へ」や時間経過でも進む受け皿なので、条件に合わない場合の遷移先として設定してください。複数の条件遷移は上から順に優先されます。',
    'PHASE', 'FLOW',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#phases',
    DATE '2026-11-10', 40, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000005',
    '入力したテキストが消えてしまいました。',
    'まずリロード（Ctrl+R / Command+R）してください。反映が遅れているだけで復旧することがあります。消えているように見える状態でテキストを触ると復活できなくなるため、操作せずに再読み込みしてください。改善しない場合は運営チームへ問い合わせてください。',
    'TEXT', 'TEXTS',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#text',
    DATE '2026-11-10', 50, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000006',
    '文字色の変更が反映されない、または黒に戻せません。',
    '黒以外の文字色から別の色に上書きした場合、エディタで即時反映されないことがあります。まずリロード（Ctrl+R / Command+R）してください。黒に戻す場合は、文字をドラッグして選択し、メニュー右端の「Tx」をクリックします。',
    'TEXT', 'TEXTS',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#text',
    DATE '2026-11-10', 60, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000007',
    '画像がアップロードできません。',
    '4.5MB以上の画像はアップロードできません。PNG（アニメーションPNGを除く）、GIF、JPEG、WebP、SVGの形式か確認し、容量が大きい場合は圧縮してください。4.5MB未満でも反映されないように見える場合はリロード（Ctrl+R / Command+R）し、改善しなければ運営チームへ問い合わせてください。',
    'MATERIAL', 'ASSETS',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#text',
    DATE '2026-11-10', 70, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000008',
    'BGMがアップロードできません。',
    'BGMは1分あたり2MB、かつ最大4.5MBまでです。超えている場合は「mp3 圧縮」などで容量を下げてからアップロードしてください。規定内でも失敗する場合は、再圧縮して再アップロードし、改善しなければ運営チームへ問い合わせてください。',
    'MATERIAL', 'BGM',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#bgm-se',
    DATE '2026-11-10', 80, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000009',
    'BGMの音量が大きすぎる、または音量調整に失敗します。',
    '音量はAudacityなどの音声編集アプリ、音量調整サイト、ウズスタジオ内の音量調整ボタンで下げられます。実際の聞こえ方は、スマートフォンでウズにアクセスし、イヤホンで再生して確認してください。複数まとめて調整して失敗する場合は本数を減らし、特定のBGMだけ失敗する場合は、音量調整で既定の容量を超えていないか確認し、圧縮して再アップロードしてください。',
    'MATERIAL', 'BGM',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#bgm-se',
    DATE '2026-11-10', 90, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000010',
    '動作確認やテストプレイで「エラーが発生しました」と表示されます。',
    '公式Q&Aに掲載されているエラー画面と同じ場合、何も書かれていないテキストボックスが原因の可能性があります。エディタで該当する空のテキストボックスを削除し、次の動作確認から再テストしてください。エラー画面が異なる場合や、削除しても直らない場合は運営チームへ問い合わせてください。',
    'CHECK', 'DEMO',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#text',
    DATE '2026-11-10', 100, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000011',
    '動作確認で最初のフェーズのBGMが再生されません。',
    'Google Chromeの自動再生制限が原因です。動作確認画面のURLバーの鍵マーク → 「サイトの設定」 → 「音声」を「許可する」に変更するか、Safariなど別ブラウザで確認してください。アプリ本番では再生されます。',
    'CHECK', 'DEMO',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#others',
    DATE '2026-11-10', 110, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000012',
    '動作確認やアプリに反映で「undefinedのフェーズ」と表示されます。',
    'アプリ側では修正できないため、運営チームへ連絡してください。公式Q&Aでも「こちらで処理が必要」と案内されています。',
    'CHECK', 'DEMO',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#others',
    DATE '2026-11-10', 120, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000013',
    '感想戦画面に置いたリンクがクリック／タップしても反応しません。',
    '感想戦のリンクは新規設置できません。URLを文字列で表示するか、二次元コード画像を設置してください。過去に設置したリンクも、動作確認ではクリックしても反応しない仕様です。',
    'CHECK', 'REFLECTION',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#result',
    DATE '2026-11-10', 130, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000014',
    '有料作品の審査通過後に無料で公開できますか？',
    'できません。有料作品の審査を通過した作品は、料金基準に沿ったプレイ料金を設定する必要があります。料金基準以上のリリースセールは実施できます。',
    'RELEASE', 'RELEASE',
    'https://docs.studio.uzu-app.com/qanda/qanda.md#others',
    DATE '2026-11-10', 140, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000015',
    'v2移行後に「アプリに反映」を押すと、一般プレイヤーへ公開されますか？',
    '公開されません。「アプリに反映」は、作者が作成するテストプレイ用イベントに反映されるだけです。一般プレイヤーが遊べる形で公開するには、v2移行申請の承認後に、リリース管理で「掲載予定版を更新」（最新版をリリース）してください。承認前はこの操作はできません。',
    'MIGRATION', 'MIGRATION',
    'https://docs.studio.uzu-app.com/qanda/qanda-v1-v2.md',
    DATE '2026-11-10', 150, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
),
(
    'a1f00000-0000-4000-8000-000000000016',
    'v2へ移行するとv1には戻せませんか？',
    'v2としてリリースした作品をv1へ戻すことはできません。ただし、移行は作品単位で、他のv1作品の編集は続けられます。また、v2版をリリースするまではv1のまま公開が継続され、移行後もv1側でシナリオの中身を確認できます。',
    'MIGRATION', 'MIGRATION',
    'https://docs.studio.uzu-app.com/qanda/qanda-v1-v2.md',
    DATE '2026-11-10', 160, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);
