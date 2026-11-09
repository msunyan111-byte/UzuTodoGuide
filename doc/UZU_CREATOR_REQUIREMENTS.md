# UZU制作ガイド・進捗管理アプリ 要求定義書

## 1. 文書情報

| 項目 | 内容 |
|---|---|
| 文書名 | UZU制作ガイド・進捗管理アプリ 要求定義書 |
| 版 | 0.1 |
| 作成日 | 2026-11-03 |
| 対象 | 個人利用向けMVP |
| ステータス | 初版 |

## 2. 背景と目的

UZU Studioでシナリオを制作する際に、公式ガイドを探す手間、制作工程の抜け漏れ、テストプレイの記録や審査準備の煩雑さを減らす。

本アプリは、公式ガイドの要点と参照先を提供し、制作者自身の作品・チェック項目・テストプレイ・審査対応を記録する。シナリオ本体の編集、UZU Studioへの申請、UZUアプリでの公開は行わない。

## 3. 想定利用者・利用環境

- 利用者は当面1名（制作者本人）とする。
- 個人のPC上で起動し、同じPCのブラウザーから利用する。
- JavaのWeb APIサーバー（AP）を中心とした構成とする。
- MVPは外部公開しない。インターネットやLANからのアクセスを前提としない。
- 認証・ユーザー登録・共同編集はMVPの対象外とする。

## 4. 用語

| 用語 | 定義 |
|---|---|
| 作品 | UZU Studioで制作する1つのシナリオ |
| 制作チェック項目 | 作品ごとに進捗を記録する工程・確認事項 |
| テストプレイ記録 | 1回分のテストプレイと結果の記録 |
| 審査記録 | 無料作品の簡易審査または有料作品の詳細審査に関する記録 |
| 公式ガイド | UZU Studio公式ドキュメント。正本は公式サイトとし、本アプリは要約とリンクを扱う |

## 5. 対象範囲

### 5.1 MVPで実現すること

1. 公式ガイドの分類・検索・詳細表示
2. 作品の登録・一覧・詳細・編集・アーカイブ
3. 作品ごとの制作工程チェックリスト
4. テストプレイとアンケートの記録
5. 審査提出・結果・修正対応の記録
6. 有料公開に向けた準備状況の確認
7. データのバックアップと復元

### 5.2 MVPでは実現しないこと

- UZU Studioのシナリオ編集機能の再実装
- UZU Studio／UZUアプリとのAPI連携、自動同期、代理申請・代理公開
- ユーザー登録、複数利用者、共同編集、権限管理
- Google Forms／Sheetsの作成・回答取得・内容の自動取り込み
- 画像・音声・動画など制作素材の保管
- 売上、印税、チケット、コレクションの管理
- 公式審査の合否予測や、作品の品質を保証する機能

## 6. 業務フロー

1. 制作者が作品を登録し、公開形態（無料／有料予定）を選択する。
2. 制作チェックリストを使い、企画・実装・動作確認の進捗を記録する。
3. 最新バージョンを作成した後、テストプレイを実施して記録する。
4. アンケート結果や改善点を記録し、必要に応じて修正と再テストを行う。
5. 審査を提出したら、提出日・審査種別・結果・指摘事項を記録する。
6. 審査通過後、公開準備と公開予定日を記録する。
7. 必要に応じて作品をアーカイブする。削除せず記録を保持する。

## 7. 機能要件

優先度は Must（MVP必須）、Should（MVPに含めることを推奨）、Could（将来候補）で表す。

### 7.1 ガイド閲覧・検索

| ID | 優先度 | 要求 |
|---|---|---|
| GDE-001 | Must | ガイドを「制作の流れ」「作品概要」「エディタ機能」「テストプレイ」「審査・公開」「FAQ」に分類して表示できる。 |
| GDE-002 | Must | ガイドのタイトル・要約・本文をキーワード検索できる。 |
| GDE-003 | Must | ガイド詳細に要約、確認日、公式ページへのリンクを表示する。 |
| GDE-004 | Should | ガイドから関連する制作チェック項目や別ガイドへ移動できる。 |
| GDE-005 | Must | 公式ドキュメントの本文をそのまま大量複製せず、要点・個人用メモ・出典URLを管理する。公式情報が変更され得ることを利用者に示す。 |

### 7.2 作品管理

| ID | 優先度 | 要求 |
|---|---|---|
| PRJ-001 | Must | 作品を新規登録できる。タイトルは必須とする。 |
| PRJ-002 | Must | 作品一覧を制作状況、公開形態、更新日で確認できる。 |
| PRJ-003 | Must | 作品のタイトル、ふりがな、ジャンル、想定人数、想定プレイ時間、公開形態、メモを登録・編集できる。 |
| PRJ-004 | Must | 作品の制作段階を設定できる。段階は「企画」「制作中」「動作確認」「テストプレイ」「修正中」「審査提出」「審査対応」「公開準備」「公開済み」とする。 |
| PRJ-005 | Must | 作品をアーカイブでき、アーカイブ後も記録を閲覧できる。 |
| PRJ-006 | Should | UZU Studioの作品ID、Studio URL、公開URLを任意で保存できる。 |

### 7.3 制作チェックリスト

| ID | 優先度 | 要求 |
|---|---|---|
| CHK-001 | Must | 作品登録時に標準チェック項目を複製し、その作品専用のチェックリストを作成する。 |
| CHK-002 | Must | チェック項目に未着手／対応中／完了、メモ、期限を設定できる。 |
| CHK-003 | Must | 項目の追加・編集・並び替えができる。 |
| CHK-004 | Must | 作品詳細に完了数と全体数を表示する。 |
| CHK-005 | Must | チェック未完了があっても記録や工程の更新を妨げない。準備不足は警告として表示する。 |
| CHK-006 | Should | 無料予定／有料予定で初期チェック項目を切り替える。 |

標準チェック項目には、少なくとも以下を含める。

- 作品概要（注意事項・あらすじ・プレイ時間など）の確認
- キャラクター・フェーズ・配布物・条件分岐の設定確認
- Studio上で最初から最後まで動作確認
- 最新バージョン作成
- テストプレイ実施とフィードバック確認
- 修正後の再確認
- 審査提出情報の準備
- 審査結果と指摘事項の反映
- 公開準備情報の確認

### 7.4 テストプレイ記録

| ID | 優先度 | 要求 |
|---|---|---|
| TST-001 | Must | 作品ごとに複数のテストプレイ記録を登録・編集・削除できる。 |
| TST-002 | Must | 実施日、実施場所（UZU／UZU外）、参加人数、テスト対象バージョン、メモ、改善点を記録できる。 |
| TST-003 | Must | アンケートの有無と提出用URLを記録できる。URLは任意とする。 |
| TST-004 | Must | テストプレイの合計回数とUZU上での回数を作品ごとに表示する。 |
| TST-005 | Must | 有料予定作品では「UZU上で1回以上を含む合計3回以上」の目安を満たしているか表示する。未達でも他の機能は利用できる。 |

### 7.5 審査・公開準備

| ID | 優先度 | 要求 |
|---|---|---|
| REV-001 | Must | 審査種別（無料向け簡易審査／有料向け詳細審査）、提出日、状態、結果、メモを記録できる。 |
| REV-002 | Must | 審査状態を未提出／提出済み／修正して再提出／クオリティ不足／審査通過で管理できる。 |
| REV-003 | Must | 指摘事項ごとに内容、対応状況、対応メモを記録できる。 |
| REV-004 | Must | 審査提出後も作品を編集できる。提出時点の内容を自動固定したと誤認させない。 |
| REV-005 | Should | 公開希望日、プレイ料金、セール予定、告知用情報を公開準備メモとして記録できる。 |

## 8. 画面要件

| 画面 | 主な内容 |
|---|---|
| ダッシュボード | 作品一覧、進行中作品、期限超過項目、テスト・審査準備の注意 |
| ガイド一覧 | カテゴリ、検索欄、検索結果 |
| ガイド詳細 | 要約、本文、確認日、公式リンク、関連項目 |
| 作品詳細 | 基本情報、工程、チェックリスト、テスト回数、審査状況 |
| 作品登録・編集 | 作品基本情報と公開形態 |
| テストプレイ | 実施記録一覧と登録・編集フォーム |
| 審査管理 | 審査履歴、指摘事項、公開準備メモ |
| 設定 | データバックアップ・復元、ガイドデータの確認日 |

すべての画面はPCブラウザーで操作できること。スマートフォン対応はShouldとする。

UZU Studio の編集画面と併用するための画面構成、ワイヤーフレーム、画面別要件、API案は `doc/UZU_STUDIO_COMPANION_UI.md` に定義する。作品ワークスペースのナビゲーションは UZU の画面・タブ名に合わせ、UZU の編集機能を再実装しない。

公式ガイドの日本語79ページを読んで整理した機能、設定項目、制約、注意点は `doc/UZU_FEATURE_INVENTORY.md` に定義する。基本／発展テンプレートの画面別提供範囲は公式に一覧がないため、推定と確定を区別して扱う。

## 9. API要件

### 9.1 共通仕様

- ベースパスは `/api/v1` とする。
- データ形式はJSON、文字コードはUTF-8とする。
- 日時はISO 8601形式で扱い、保存・表示の基準タイムゾーンは `Asia/Tokyo` とする。
- リソースIDはUUIDとする。
- 一覧APIはMVPでは少量データを想定し、必要に応じて `limit` と `offset` を受け付ける。
- `Content-Type: application/json` を使用する。
- 成功時は適切なHTTPステータスコードとリソースを返す。
- バリデーションエラー等は後述の共通エラー形式で返す。

### 9.2 エンドポイント

| Method | Path | 用途 |
|---|---|---|
| GET | `/health` | APの稼働確認 |
| GET | `/guides` | ガイド一覧・キーワード検索・カテゴリ絞り込み |
| GET | `/guides/{guideId}` | ガイド詳細 |
| GET | `/guide-categories` | ガイドカテゴリ一覧 |
| GET | `/projects` | 作品一覧。`status`、`archived` で絞り込み |
| POST | `/projects` | 作品登録と標準チェックリスト作成 |
| GET | `/projects/{projectId}` | 作品詳細 |
| PATCH | `/projects/{projectId}` | 作品情報・制作段階の更新 |
| POST | `/projects/{projectId}/archive` | 作品アーカイブ |
| GET | `/projects/{projectId}/checklist` | チェックリスト取得 |
| POST | `/projects/{projectId}/checklist` | チェック項目追加 |
| PATCH | `/projects/{projectId}/checklist/{itemId}` | チェック項目の状態・メモ等の更新 |
| DELETE | `/projects/{projectId}/checklist/{itemId}` | チェック項目削除 |
| GET | `/projects/{projectId}/playtests` | テストプレイ記録一覧 |
| POST | `/projects/{projectId}/playtests` | テストプレイ記録登録 |
| PATCH | `/projects/{projectId}/playtests/{playtestId}` | テストプレイ記録更新 |
| DELETE | `/projects/{projectId}/playtests/{playtestId}` | テストプレイ記録削除 |
| GET | `/projects/{projectId}/reviews` | 審査記録一覧 |
| POST | `/projects/{projectId}/reviews` | 審査記録登録 |
| PATCH | `/projects/{projectId}/reviews/{reviewId}` | 審査記録・指摘事項更新 |
| GET | `/projects/{projectId}/reviews/{reviewId}/issues` | 審査指摘事項一覧 |
| POST | `/projects/{projectId}/reviews/{reviewId}/issues` | 審査指摘事項登録 |
| PATCH | `/projects/{projectId}/reviews/{reviewId}/issues/{issueId}` | 指摘事項と対応状況の更新 |
| DELETE | `/projects/{projectId}/reviews/{reviewId}/issues/{issueId}` | 指摘事項削除 |
| GET | `/backup` | DBデータのJSONエクスポート |
| POST | `/restore` | JSONバックアップからの復元 |

上表のパスはベースパス `/api/v1` からの相対パスとする。

### 9.3 作品APIの例

`POST /api/v1/projects`

```json
{
  "title": "夜明けの証言",
  "furigana": "よあけのしょうげん",
  "genre": "マーダーミステリー",
  "playerCount": 5,
  "estimatedMinutes": 180,
  "releaseType": "PAID",
  "scoringRule": "TIE_ALLOWED",
  "studioUrl": "https://studio.uzu-app.com/"
}
```

成功時は `201 Created` を返す。

```json
{
  "id": "d077d025-4b79-4c48-b00f-d5cfc20a606d",
  "title": "夜明けの証言",
  "furigana": "よあけのしょうげん",
  "genre": "マーダーミステリー",
  "playerCount": 5,
  "estimatedMinutes": 180,
  "releaseType": "PAID",
  "stage": "PLANNING",
  "scoringRule": "TIE_ALLOWED",
  "archived": false,
  "createdAt": "2026-11-03T13:00:00+09:00",
  "updatedAt": "2026-11-03T13:00:00+09:00"
}
```

`releaseType` は `FREE` または `PAID` とする。`scoringRule` は `TIE_ALLOWED`（同数最多も可）または `SOLE_TOP_REQUIRED`（単独最多のみ）とし、既定は `TIE_ALLOWED` とする。`stage` は以下の値を使用する。

| API値 | 表示名 |
|---|---|
| `PLANNING` | 企画 |
| `IN_PROGRESS` | 制作中 |
| `FUNCTIONAL_CHECK` | 動作確認 |
| `PLAYTEST` | テストプレイ |
| `REVISION` | 修正中 |
| `REVIEW_SUBMITTED` | 審査提出 |
| `REVIEW_RESPONSE` | 審査対応 |
| `RELEASE_PREPARATION` | 公開準備 |
| `RELEASED` | 公開済み |

チェック項目の状態は `NOT_STARTED`、`IN_PROGRESS`、`DONE` とする。審査状態は `DRAFT`（未提出）、`SUBMITTED`（提出済み）、`REVISION_REQUESTED`（修正して再提出）、`QUALITY_INSUFFICIENT`（クオリティ不足）、`PASSED`（審査通過）とする。

### 9.4 テストプレイAPIの例

`POST /api/v1/projects/{projectId}/playtests`

```json
{
  "playedAt": "2026-11-02",
  "platform": "UZU",
  "participantCount": 5,
  "versionNote": "第3版・投票分岐修正後",
  "questionnaireCollected": true,
  "questionnaireUrl": "https://docs.google.com/spreadsheets/d/example",
  "findings": "終盤の情報提示が分かりにくいとの意見"
}
```

`platform` は `UZU` または `OTHER` とする。URL未入力でも記録できる。

### 9.5 エラー応答

エラー応答は次の形式とする。

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力内容を確認してください。",
    "details": [
      {
        "field": "title",
        "reason": "必須項目です。"
      }
    ]
  }
}
```

| HTTP status | 用途 |
|---|---|
| 400 | JSON形式不正、入力値不正 |
| 404 | 対象リソースが存在しない |
| 409 | 重複や状態競合など、要求を処理できない |
| 500 | 想定外のサーバーエラー。内部情報やスタックトレースは返さない |

## 10. データ要件

| データ | 主な項目 |
|---|---|
| Project | ID、タイトル、ふりがな、ジャンル、人数、想定時間、公開形態、工程、得点ルール、Studio URL、メモ、アーカイブ状態、作成・更新日時 |
| ChecklistItem | ID、作品ID、名称、説明、状態、メモ、期限、表示順 |
| PlaytestRecord | ID、作品ID、実施日、場所、参加人数、バージョンメモ、アンケート有無・URL、改善点 |
| ReviewRecord | ID、作品ID、審査種別、提出日、状態、結果、公開準備メモ |
| ReviewIssue | ID、審査記録ID、指摘内容、対応状態、対応メモ |
| GuideArticle | ID、タイトル、カテゴリ、要約、本文、公式URL、確認日、更新日時 |

- 作品をアーカイブしても関連するチェック・テスト・審査記録を保持する。
- 関連リソースを削除する場合は、親作品を誤って削除しないよう明示的な操作とする。
- バックアップにはガイド本文の外部コンテンツではなく、利用者の記録とガイドのメタデータを含める。
- 復元時はデータ形式・バージョンを検証し、失敗時に既存DBを部分的に破壊しない。

## 11. 非機能要件

### 11.1 構成・運用

- APはJava 11以上およびSpring Boot 2.7を前提とする。外部Tomcat 9で動作するWARを生成する。
- Web APIはREST形式とし、永続化にはSpring Data JPAを用いる。
- DBはPostgreSQL 15をDocker Composeで起動する。テストはTestcontainersで同じDBを用いる。
- スキーマ変更はFlywayのバージョン管理されたマイグレーションで行う。
- バックアップはJSONで入出力し、復元時は形式を検証する。
- 起動方法・バックアップ方法・復元方法をREADMEに記載する。

### 11.2 セキュリティ・プライバシー

- APは既定で `127.0.0.1` にのみバインドし、外部ネットワークから接続できないようにする。
- 認証を実装しない間は、LAN・インターネットへ公開する設定をサポート対象外とする。
- ログに作品本文、アンケート回答、秘密情報を出力しない。
- URLを保存しても、リンク先へ自動アクセスしたり内容を自動取得したりしない。
- 復元APIはローカル利用に限定し、形式検証と事前バックアップを行う。

### 11.3 品質

- 作品・チェック項目・テスト記録・審査記録の主要APIに自動テストを用意する。
- 不正な入力、存在しないID、空の一覧、バックアップ復元失敗をテストする。
- 個人利用の通常規模で、一覧・詳細表示が体感上待たされないことを目標とする。

## 12. 受け入れ条件

1. 作品を登録すると、その作品用の標準チェックリストが作成される。
2. 作品の工程、チェック項目、期限、メモを登録・更新できる。
3. UZU／UZU外のテストプレイ記録を追加でき、件数が区別して表示される。
4. 有料予定作品でUZU上1回かつ合計3回未満の場合、準備不足が表示されるが登録操作は妨げられない。
5. 審査種別・提出日・結果・指摘事項を記録し、再提出後の状態も更新できる。
6. ガイドをキーワード検索でき、各ガイドから公式ページを開ける。
7. アーカイブした作品と関連記録を閲覧できる。
8. バックアップを書き出し、同じ環境へ復元して作品・チェック・テスト・審査記録が保持される。
9. APがローカルホスト以外から接続できない既定設定で起動する。

## 13. 公式情報の根拠

公式ガイドは更新されるため、本アプリの要約・チェック項目は公式情報の代替ではない。重要な判断ではリンク先の最新情報を確認する。

- [UZU STUDIO公式ガイド目次](https://docs.studio.uzu-app.com/llms.txt)
- [作成からリリースまでの流れ](https://docs.studio.uzu-app.com/overview/makingflow.md)
- [テストプレイ](https://docs.studio.uzu-app.com/overview/makingflow/testplay.md)
- [詳細審査（有料公開作品）](https://docs.studio.uzu-app.com/overview/makingflow/submit/check.md)
- [UZU Studio v2でできること・できないこと](https://docs.studio.uzu-app.com/overview/available.md)
- [ホーム全体について](https://docs.studio.uzu-app.com/scenariohome/main-screen.md)
- [リリース管理](https://docs.studio.uzu-app.com/scenariohome/release.md)
