-- サンプルシナリオへ制作講座由来のTODOを追加し、既存TODOの説明を公式ガイドに合わせる。
UPDATE checklist_items
SET description = 'UZU Studio にログインし、「作品登録」を開きます。テンプレート（基本／発展）を選び、ジャンル、タイトル、ふりがなを入力します。タイトルが未定なら「仮」でも構いません。プレイ時間は「作品概要」、公開形態は審査提出時に設定します。完了条件: 作品一覧に作品が表示され、「作品概要」を開ける状態になっている。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【作品登録】作品の基本情報を登録する';

UPDATE checklist_items
SET description = '「作品概要」を開き、プレイヤーが最初に見る情報を入力します。プレイ時間は集合から解散まで（キャラクター選択と感想戦を含む）で設定し、ふりがなはひらがなのみ、短縮タイトル・作家・タグ・シナリオ傾向・注意事項・配役のヒント・配信許可も内容に合わせます。完了条件: あらすじ、注意事項、作者コメント、プレイ時間、配役のヒントが実際の内容と一致している。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【作品概要】あらすじ・注意事項・作者コメントを入力する';

UPDATE checklist_items
SET description = '「作品概要」で16:9のキービジュアルを設定します。アプリ一覧では160×90で表示されるため、縮小してもタイトルが読めるか、背景と文字のコントラストが十分か、タイトル全文が画像内に入っているか、利用権があるかを確認します。難読タイトルは画像内のふりがなも確認し、サブ画像を追加する場合は表示順も決めます。完了条件: 作品一覧と作品詳細で画像が崩れず、タイトルが読め、素材の権利を説明できる。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【作品概要】キービジュアルとサブ画像を設定する';

UPDATE checklist_items
SET description = '「キャラクター」を開き、プレイヤーが演じるキャラクターを登録します。名前、説明、アイコン、ニックネームは必須、ふりがなは任意です。NPCを追加する場合は一覧表示のON/OFFを決め、条件付きアイコン・ニックネームとエンドカード（16:9）も必要に応じて設定します。完了条件: 作品で決めた人数分のキャラクターが表示され、必須項目と名前の表記が全画面で揃っている。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【キャラクター】プレイヤーキャラクターを登録する';

UPDATE checklist_items
SET description = '「フェーズ」を開き、基本・読み合わせ・ゲームの流れ表示・探索から種類を選び、導入、調査、議論、投票、感想戦などの流れに沿って追加します。フェーズを追加・削除したら「進行設定」と「ゲームの流れ」の両方を繋ぎ直します。完了条件: 最初から最後までのフェーズが一覧とゲームの流れに並び、各フェーズの目的を説明できる。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【フェーズ】フェーズを作成する';

UPDATE checklist_items
SET description = '「テキスト」を開き、本文、ふりがな、字下げ、改行、表示条件を入力します。テキストグループで「共通」「個別」「読み合わせ」を分け、フェーズ内容の記録では同期テキストとコピーテキストの違いを確認します。完了条件: 誤字脱字・表記揺れがなく、スマホ幅でも読みやすい。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【テキスト】本文・ふりがな・表示条件を入力する';

UPDATE checklist_items
SET description = '各手がかりを、誰に、いつ、どの条件で配布するか設定します。配布時にポップアップを出す場合は「所有権（閲覧権）→通知を送信」の順にし、未公開時のタイトル・タグの見え方、回収条件、全体公開・共有・譲渡の可否も確認します。完了条件: 推理に必要な手がかりが全員に届き、所有権・通知順・公開順が矛盾していない。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【手がかり】公開条件・配布先・強制公開を設定する';

UPDATE checklist_items
SET description = '「動作確認」を開き、最初から最後まで通しで再生します。セリフ、テキストや手がかりの配布先・条件、密談の可否、投票によるエンディング分岐、BGM/SEの設定を確認し、エラー一覧から該当箇所へ移動して直します。完了条件: 最後まで止まらず、各キャラクターで必要な情報が見える。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【動作確認】全キャラクターで通し確認する';

UPDATE checklist_items
SET description = 'Googleアカウントを用意し、公式テンプレートでアンケートをコピーします。初期項目を削除せず、タイトル・キャラクター名・人数を調整して回収します（質問の追加は自由）。回答はスプレッドシート化し、「リンクを知っている全員」が閲覧できる共有リンクを準備します。完了条件: 指摘ごとに対応方針が決まり、提出できる共有リンクがある。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【テストプレイ】アンケートを回収し改善点を反映する';

UPDATE checklist_items
SET description = '「リリース管理」のアプリ掲載申請から、審査種別、公開希望日、料金、告知情報を確認します。セール希望は公式Discordで運営に連絡してから提出します。有料公開はUZU上1回以上を含む合計3回以上のテストプレイとアンケート回収が前提です。提出後も作品は編集できます。完了条件: テスト回数・アンケート・提出情報がそろい、提出済みになっている。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【審査・公開】審査提出情報を準備する';

UPDATE checklist_items
SET description = '簡易審査（無料作品）・詳細審査（有料作品）ともに、土日祝を除く5営業日程度が目安です。指摘事項を修正し、必要なら再提出します。完了条件: 指摘がすべて対応済みで、再提出または公開可能な状態になっている。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【審査・公開】審査結果と指摘事項を反映する';

UPDATE checklist_items
SET name = '【リリース管理】料金・セール・公開日の希望を確認する',
    description = '「リリース管理」で公開希望日を決めます。プレイ料金は公開準備用フォームで回答し、料金変更やセールは公式Discordの個別チャンネルで運営に連絡します。セール期間は最大1か月、セール料金は設定可能な最低料金から100円（70コイン）引きまでです。完了条件: 料金・セール・公開日の希望を説明でき、公開準備フォームの回答内容が作品概要と一致している。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【リリース管理】料金・セール・公開日を設定する';

UPDATE checklist_items
SET description = '「リリース管理」で公開希望日、公開状態、料金、タグ、注意事項、キービジュアル、審査結果を最終確認します。キービジュアルは16:9・160×90で読める・タイトル全文入り・権利確認済みを再確認します。完了条件: 公開ボタンを押せる状態で、公開後のレポート確認方法も分かっている。',
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND name = '【リリース管理】公開状態を最終確認する';

UPDATE checklist_items
SET display_order = display_order + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND display_order >= 2;

UPDATE checklist_items
SET display_order = display_order + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND display_order >= 6;

UPDATE checklist_items
SET display_order = display_order + 1,
    updated_at = CURRENT_TIMESTAMP
WHERE project_id = '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c'
  AND display_order >= 20;

INSERT INTO checklist_items (
    id, project_id, name, description, status, memo, due_date, display_order, created_at, updated_at
) VALUES
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000353', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【作品概要】プロット0（事件・犯人・動機・配役）を固める',
     '「作品概要」を書く前に、レクチャーの制作フォーマットに沿って、事件の入口、犯人、動機、トリック、配役、決着を1枚にまとめます。基本は3人、必要に応じて4〜5人へ拡張し、誰が何を隠し、どの手がかりで真相に届くかを説明できる状態にします。完了条件: PC人数と役割、事件・犯人・動機・トリック・各PCの目的が1文ずつあり、テストプレイ可能な結末まで決まっている。',
     'NOT_STARTED', NULL, NULL, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000354', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【キャラクター】人物の関係性と秘密の見せ場を整理する',
     '「キャラクター」を登録する前に、全プレイヤーキャラクターについて、表向きの立場、隠したいこと、誰に知られたくないか、最後に言いたい一言を整理します。関係性は「信頼」「疑い」「片思い」などの向きを決め、どのフェーズで表に出るかを配布テキストと手がかりに紐づけます。完了条件: 各PCの秘密と関係性を1文で説明でき、真相に必要な情報が誰の視点から出るか決まっている。',
     'NOT_STARTED', NULL, NULL, 6, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('7f1c2b3a-4d5e-4f6a-8b9c-000000000355', '7f1c2b3a-4d5e-4f6a-8b9c-0d1e2f3a4b5c',
     '【テキスト】公開前の文章ルールを確認する',
     '公開前に、補助動詞（〜していただく、〜してください）や「こと」「とき」のひらがな表記、ダッシュ、三点リーダー、！？後のスペース、読点・句点、字下げ、表記揺れを確認します。キャラクターテキストと地の文では字下げを統一し、スマホ幅で読み返します。完了条件: 公式の「文章のよくある指摘」に沿って全文を確認し、修正漏れがない。',
     'NOT_STARTED', NULL, NULL, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
