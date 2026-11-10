# uzu-todo-guide

UZU Studio の制作ガイド要点整理と、作品・チェックリスト・テストプレイ・審査記録・バックアップを扱うローカル向け API 基盤、および制作伴走用の React UI です。

要件定義 `doc/UZU_CREATOR_REQUIREMENTS.md` より、公式ガイド `https://docs.studio.uzu-app.com/` の内容を優先して実装しています。UZU Studio の編集画面と併用する画面・要件は `doc/UZU_STUDIO_COMPANION_UI.md`、公式ガイド全79ページの機能・制約の整理は `doc/UZU_FEATURE_INVENTORY.md` にまとめています。UZU Studio で初めてシナリオを作る人を想定し、TODO は画面名・操作手順・完了条件が分かる形にしています。

開発に参加する際は `AGENTS.md` のコーディング規約・テスト手順に従ってください。

## 最初に実施すること

初回セットアップとして、以下を順に確認・実行してください。

1. **必要なソフトウェアを用意する**
   - JDK 11 または 17（`JAVA_HOME` を設定）
   - Docker Desktop（PostgreSQL と Testcontainers 用）
   - Node.js 20 以上 / npm
   - Maven は不要（`./mvnw` を使用）
2. **このリポジトリを取得し、ルートディレクトリへ移動する**
3. **フロントエンドの依存関係をインストールする**

   ```bash
   cd frontend
   npm ci
   npx playwright install chromium   # UIテストを実行する場合、初回のみ
   cd ..
   ```

4. **Docker Desktop を起動する**
5. **開発ルールを確認する**（`AGENTS.md`）

> Spring Boot 2.7 は JDK 11/17 を推奨。JDK 21 以降は未検証ですが、JDK 26 でもテストは通っています。

## 起動手順

### 1. PostgreSQL を起動する

```bash
docker compose up -d
```

### 2. バックエンドを起動する

```bash
./mvnw spring-boot:run
```

`server.address` は既定で `127.0.0.1`、ポートは `8080` です。

DB 接続は環境変数で上書きできます。

```bash
DB_URL=jdbc:postgresql://localhost:5432/todo \
DB_USERNAME=todo \
DB_PASSWORD=todo \
./mvnw spring-boot:run
```

### 3. フロントエンド開発サーバーを起動する

```bash
cd frontend
npm run dev
```

`http://127.0.0.1:5173` で開けます。開発時は `/api` を `8080` へ proxy するため、バックエンドも起動しておいてください。

### 4. 画面を開く

- 開発時: `http://127.0.0.1:5173`
- Spring Boot からビルド済み UI を配信する場合: `http://127.0.0.1:8080`

ビルド済み UI を Spring Boot から配信する場合は、先にフロントエンドをビルドします。

```bash
cd frontend
npm run build                 # src/main/resources/static へ出力
cd ..
./mvnw spring-boot:run
```

- 画面は Hash Router のため、静的配信でもリロード時に 404 になりません。
- API に接続できない場合は、デモ作品「吸血鬼の館のマーダーミステリー」を表示します。デモ中の変更はブラウザ内だけに保持されます。
- セクションの短いメモと最終確認日は `localStorage` に保存します。作品・チェックリスト・バックアップは API / DB に保存します。
- 実装済み画面: ダッシュボード、作品一覧、作品新規作成、作品ワークスペース（セクション別チェック・次アクション・公開準備・作品編集）、UZU機能ガイド（44セクション検索）、設定・バックアップ、コンパクトモード。

## テスト

### バックエンド

```bash
./mvnw test                                   # 単体・結合テスト（e2e タグは除外）
./mvnw test -Dsurefire.excludedGroups=        # Playwright Java の API E2E も実行
```

Java E2E は Playwright の API リクエストのみで、ブラウザのインストールは不要です。
Testcontainers が PostgreSQL コンテナを自動起動するため Docker が必要です。

### フロントエンド

初回は「最初に実施すること」の `npm ci` と Chromium のインストールを済ませてください。

```bash
cd frontend
npm run lint
npm run typecheck                 # アプリ + E2E の型チェック
npm run build
npm run test:e2e
```

UI E2E は `frontend/e2e/` にあり、バックエンド不要で動作します（`installApiMock` が `/api/v1/**` をモック）。
画面を変更したら対応する `*.spec.ts` を更新・追加し、全件通ることを確認してください。

### CI

`.github/workflows/ci.yml` で以下を実行します。

- `junit`: `./mvnw -B test -Dsurefire.excludedGroups=`（Java E2E 含む）
- `playwright`: lint / typecheck / build / `npm run test:e2e`（レポートを artifact 保存）

## 技術スタック

| 分類 | 内容 |
| --- | --- |
| 言語 | Java 11 |
| フレームワーク | Spring Boot 2.7.18 (Spring MVC / Spring Data JPA) |
| サーブレット | Tomcat 9 (Servlet 4.0 / `javax.*`) |
| ビルド | Maven (Wrapper 同梱) |
| DB | PostgreSQL 15 (Docker Compose) / Flyway |
| フロントエンド | React 18 / TypeScript 5 / Vite 6 |
| 単体・結合テスト | JUnit 5 / Mockito / MockMvc / Testcontainers |
| E2E テスト | Playwright Java (API リクエスト) / Playwright (UI) |

## 構成

```
.
├── pom.xml
├── mvnw / mvnw.cmd / .mvn/wrapper/
├── docker-compose.yml
├── AGENTS.md                            # エージェント／開発者向け規約
├── .github/workflows/ci.yml             # JUnit + Playwright CI
├── frontend/                            # React + Vite UI
│   ├── src/
│   │   ├── pages/                       # ダッシュボード / 作品 / ワークスペース / ガイド / 設定 / コンパクト
│   │   ├── components/                  # 共通UI
│   │   ├── data/uzu.ts                  # 44セクション・制約・確認ヒント
│   │   └── data/demo.ts                 # API未起動時のデモデータ
│   ├── e2e/                             # Playwright UI テスト
│   ├── playwright.config.ts
│   └── vite.config.ts                   # /api proxy / static 出力
├── src/main/java/com/example/uzu/
│   ├── UzuTodoApplication.java          # 起動クラス
│   ├── ServletInitializer.java          # 外部 Tomcat 用
│   ├── domain/                          # Project / Checklist / Guide / Playtest / Review / ScoringRule
│   ├── repository/                      # Spring Data JPA
│   ├── service/
│   │   ├── ProjectService.java
│   │   ├── ChecklistService.java
│   │   ├── StandardChecklist.java       # 公式ガイド準拠の標準項目
│   │   ├── GuideService.java            # 公式ガイド要約の初期投入
│   │   ├── PlaytestService.java
│   │   ├── ReviewService.java
│   │   └── BackupService.java
│   ├── support/
│   │   └── TokyoTime.java
│   └── web/
│       ├── ProjectController.java
│       ├── ChecklistController.java
│       ├── GuideController.java
│       ├── PlaytestController.java
│       ├── ReviewController.java
│       ├── BackupController.java
│       ├── dto/
│       └── error/                       # 共通エラーハンドラ
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       ├── V1__init.sql
│       ├── V2__add_uzu_records.sql
│       ├── V3__seed_vampire_mansion_scenario.sql  # 動作確認用サンプル
│       ├── V4__refine_vampire_mansion_scenario.sql # シナリオ整合性の更新
│       ├── V5__add_scoring_rule_to_projects.sql    # 得点ルール設定
│       ├── V6__restructure_vampire_mansion_todos.sql # TODOの画面名・完了条件整理
│       └── V7__add_editor_page_todos.sql           # NPC・演出配下TODOの追加
└── src/test/java/com/example/uzu/
    ├── service/ProjectServiceTest.java
    ├── service/ChecklistServiceTest.java
    ├── service/StandardChecklistTest.java
    ├── service/ServiceValidationTest.java
    ├── web/ProjectControllerTest.java
    ├── web/ChecklistControllerTest.java
    ├── ProjectApiIntegrationTest.java        # Testcontainers
    ├── SupportingApiIntegrationTest.java     # Testcontainers（補助API）
    └── e2e/ProjectApiE2ETest.java            # Playwright (tag: e2e)
```

## 初期データ

- 公式ガイドの要約記事 8 件（`guide_articles`）は起動時に投入されます。既に記事がある場合はスキップされます。
- 動作確認用のサンプルシナリオ「吸血鬼の館のマーダーミステリー」は Flyway `V3` で DB に登録されます。
  - PAID / PLANNING / 4人 / 90分
  - 有料作品用の標準チェックリスト 41 件
  - シナリオ固有の作成 TODO 10 件
  - 合計 51 件。未確定の設計項目は memo に `未確定` と記録
- `V4` でサンプルのシナリオ整合性を更新します（殺人未遂、施錠時刻、ランドルフの吸血鬼ハンター設定、ルーシィの復讐対象など）。
- `V5` で作品ごとの得点ルール `scoring_rule` を追加します。既存作品とサンプルは同数最多を許可する `TIE_ALLOWED` です。
- `V6` でサンプルの TODO を、UZU Studio の画面名を先頭に付けた完成手順へ再構成します。
- `V7` でサンプルにNPCと演出配下（BGM・SE・ムービー・背景画像・カラーテーマ）のTODOを追加します。

サンプルシナリオはアプリ本体のコードには組み込んでいません。他のシナリオは `POST /api/v1/projects` で通常どおり作成できます。サンプルが不要な場合は作品をアーカイブまたは削除してください。

標準チェックリストは FREE 39 件 / PAID 41 件です。PAID では規約同意と売上・振込の確認が追加されます。項目名の先頭には UZU Studio の画面名を【】付きで表示し、初めて制作する人でも「どの画面で何をするか」と「完了条件」を追えるようにしています。

## API

ベースパスは `/api/v1` です。

### ヘルスチェック

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/health` | `{"status":"UP"}` |
| GET | `/actuator/health` | Actuator ヘルスチェック |

### 作品

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/projects?status=PLAYTEST&archived=false` | 一覧。`status` と `archived` は任意 |
| POST | `/api/v1/projects` | 作成 (201)。標準チェックリストも同時作成 |
| GET | `/api/v1/projects/{projectId}` | 1件取得 |
| PATCH | `/api/v1/projects/{projectId}` | 部分更新 |
| POST | `/api/v1/projects/{projectId}/archive` | アーカイブ |

主な項目: `title`, `furigana`, `genre`, `playerCount`, `estimatedMinutes`, `releaseType` (`FREE`/`PAID`), `stage`, `studioWorkId`, `studioUrl`, `publicUrl`, `scoringRule`, `memo`。

`scoringRule` は得点条件「最多にならない」の判定方法を作品ごとに選びます。

| 値 | 意味 |
| --- | --- |
| `TIE_ALLOWED` | 単独最多でなく、同数最多でも条件を満たす（既定） |
| `SOLE_TOP_REQUIRED` | 単独最多である場合のみ条件を満たす |

`ProjectResponse` には `checklistCompleted` と `checklistTotal` が含まれます。

### チェックリスト

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/projects/{projectId}/checklist` | 一覧 |
| POST | `/api/v1/projects/{projectId}/checklist` | 追加 (201) |
| PATCH | `/api/v1/projects/{projectId}/checklist/{itemId}` | 更新 |
| DELETE | `/api/v1/projects/{projectId}/checklist/{itemId}` | 削除 (204) |

`status` は `NOT_STARTED` / `IN_PROGRESS` / `DONE` です。

### ガイド

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/guides?category=PLAYTEST&keyword=アンケート` | 一覧。カテゴリ・キーワードは任意 |
| GET | `/api/v1/guides/{guideId}` | 1件取得 |
| GET | `/api/v1/guide-categories` | カテゴリ一覧 |

### テストプレイ

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/projects/{projectId}/playtests` | 一覧 |
| POST | `/api/v1/projects/{projectId}/playtests` | 追加 (201) |
| PATCH | `/api/v1/projects/{projectId}/playtests/{playtestId}` | 更新 |
| DELETE | `/api/v1/projects/{projectId}/playtests/{playtestId}` | 削除 (204) |

主な項目: `playedAt`, `platform`, `participantCount`, `versionNote`, `questionnaireCollected`, `questionnaireUrl`, `findings`, `memo`。

### 審査

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/projects/{projectId}/reviews` | 審査記録一覧 |
| POST | `/api/v1/projects/{projectId}/reviews` | 審査記録追加 (201) |
| PATCH | `/api/v1/projects/{projectId}/reviews/{reviewId}` | 審査記録更新 |
| GET | `/api/v1/projects/{projectId}/reviews/{reviewId}/issues` | 指摘事項一覧 |
| POST | `/api/v1/projects/{projectId}/reviews/{reviewId}/issues` | 指摘事項追加 (201) |
| PATCH | `/api/v1/projects/{projectId}/reviews/{reviewId}/issues/{issueId}` | 指摘事項更新 |
| DELETE | `/api/v1/projects/{projectId}/reviews/{reviewId}/issues/{issueId}` | 指摘事項削除 (204) |

### バックアップ

| メソッド | パス | 説明 |
| --- | --- | --- |
| GET | `/api/v1/backup` | 全データを JSON で出力 |
| POST | `/api/v1/restore` | JSON から復元 |

### エラー形式

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "入力内容に誤りがあります。",
    "details": [
      { "field": "title", "reason": "必須項目です。" }
    ]
  }
}
```

## 画面設計

UZU Studio の基本／発展テンプレート画面に合わせ、編集画面の横で使う伴走型 UI の画面一覧、ワイヤーフレーム、要件、API 案を `doc/UZU_STUDIO_COMPANION_UI.md` に定義しています。公式ガイドの日本語79ページを読んで整理した機能・設定項目・制約・注意点は `doc/UZU_FEATURE_INVENTORY.md` にあります。

React UI は `frontend/` に実装済みです。設計上の未決事項と今後の拡張は `doc/UZU_STUDIO_COMPANION_UI.md` の「検証・未決事項」を参照してください。

## Tomcat 9 へのデプロイ

```bash
./mvnw clean package
```

生成物 `target/uzu-todo.war` を Tomcat 9 の `webapps/` に配置します。
`spring-boot-starter-tomcat` は `provided` のため WAR には含まれません。
コンテキストパスは WAR 名に従い `/uzu-todo` になります。

## 設定のポイント

- `spring.jpa.hibernate.ddl-auto=validate` でスキーマ変更は Flyway に一元化
- `spring.jpa.open-in-view=false` で遅延ロードをトランザクション内に限定
- `spring-boot-starter-validation` + `@RestControllerAdvice` で入力エラーを JSON 化
- Jackson / JDBC のタイムゾーンを `Asia/Tokyo` に統一
- Testcontainers により本番同等の PostgreSQL でテスト

## 公式ガイド参照メモ

標準チェックリストと初期ガイドは、以下の公式ページを優先して整理しています。

- `https://docs.studio.uzu-app.com/llms.txt`
- `overview/makingflow.md`
- `overview/makingflow/testplay.md`
- `overview/makingflow/submit.md`
- `overview/makingflow/submit/check.md`
- `overview/makingflow/submit/questionnaire.md`
- `overview/makingflow/submit/common-mistake.md`
- `visual-regulation.md`
- `basic-features/demo.md`
- `basic-features/new-version.md`
- `others/sale.md`
- `scenariohome/overview.md`
- `scenariohome/release.md`
- `overview/template/basic`
- `overview/template/advanced`
