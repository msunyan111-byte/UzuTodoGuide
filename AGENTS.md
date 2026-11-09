# AGENTS.md

このファイルは、このリポジトリで作業する AI エージェント／開発者向けの指示です。作業前に必ず目を通してください。

## プロジェクト概要

UZU Studio の制作ガイド整理、作品・チェックリスト・テストプレイ・審査・バックアップを扱うローカル向け API と、制作伴走用 React UI のリポジトリです。

- 要件: `doc/UZU_CREATOR_REQUIREMENTS.md`
- 伴走 UI 要件: `doc/UZU_STUDIO_COMPANION_UI.md`
- 公式ガイドの機能整理: `doc/UZU_FEATURE_INVENTORY.md`
- 使い方・API 一覧: `README.md`

## 技術スタック

- Java 11 / Spring Boot 2.7.18 / Spring MVC / Spring Data JPA / PostgreSQL 15 / Flyway
- React 18 / TypeScript 5 / Vite 6 / React Router 7（HashRouter）
- テスト: JUnit 5 / Mockito / MockMvc / Testcontainers / Playwright
- ビルド: Maven Wrapper（`./mvnw`）と npm

## コーディング規約

### Java

必ず [Fintan Java Style Guide](https://github.com/Fintan-contents/coding-standards/blob/main/java/java-style-guide.md) に従う。

- Java 11 の構文で書く。record や sealed など Java 12 以降の構文は使わない。
- クラス・フィールド・メソッドには目的が分かる Javadoc を付ける。IDE 生成の空コメントは残さない。
- Javadoc・コメントは日本語で書き、常用漢字を使う。読み手が処理の意図を追える内容にする。
- テストメソッドには `@DisplayName` を付け、日本語で「何を検証するか」を表す。
- 命名は `PascalCase`（クラス）、`camelCase`（メソッド・変数）、`UPPER_SNAKE_CASE`（定数）。
- ローカル変数・引数は可能な限り `final` にする。コレクションは `List.of` / `Map.of` / `Set.of` を活用する。
- マジックナンバー・マジックストリングを避け、定数または enum に置き換える。
- ガード節と早期 return を使い、ネストを深くしない。
- 未使用のクラス・メソッド・フィールド・import を残さない。
- `null` を返す API を増やさない。コレクションは空コレクションを返す。
- 例外を握りつぶさない。業務例外は既存の例外ハンドラ／エラー形式に合わせる。
- `System.out` / `printStackTrace()` を本番コードで使わない。ログは SLF4J を使う。
- 日時は `TokyoTime` など既存のユーティリティを通す。タイムゾーンを暗黙に扱わない。
- 既存の共通化を使う。監査項目を持つエンティティは `AuditedEntity`、作品存在チェックは `ServiceValidation.requireProjectExists` を使う。
- DB 変更は Flyway の新規マイグレーション（`V{次の番号}__説明.sql`）で行う。適用済みマイグレーションは書き換えない。

### React / TypeScript

必ず [React のルール](https://ja.react.dev/reference/rules) に従う。

- コンポーネントとフックは純粋に保つ。render 中に副作用を起こさない。
- props と state を直接変更しない。イミュータブルに更新する。
- フックはトップレベルでのみ呼ぶ。条件分岐・ループ・ネストした関数の中で呼ばない。
- `useEffect` は外部システムとの同期にのみ使う。購読・タイマー・イベントリスナーには必ずクリーンアップを書く。
- props から計算できる値を state に複製しない。render 中に導出する。
- イベントハンドラを render 中に呼び出さない。
- リストの `key` には安定した ID を使う。動的リストで配列 index を key にしない。
- `eslint-plugin-react-hooks` の指摘を無効化しない。やむを得ず無効化する場合は理由をコメントに残す。
- TypeScript の `strict` を維持する。`any` は原則禁止。外部入力の境界では `unknown` から型を絞る。
- 型は既存の `types.ts` に集約し、表示ラベルは `STAGE_LABELS` / `RELEASE_LABELS` / `SCORING_LABELS` などの定数を使う。
- API 呼び出しは既存の API クライアントに追加する。画面コンポーネントから直接 `fetch` を増やさない。
- 画面は既存のレイアウト・コンポーネント・CSS クラスを再利用する。UI 文言は日本語に合わせる。
- 画面テストは `test.describe` / `test` 名を日本語の仕様として書き、必要に応じて `test.step` で操作を分割する。
- `src/main/resources/static/` は `npm run build` の生成物。手作業で編集しない。

## ディレクトリと設計

### バックエンド

```text
src/main/java/com/example/uzu/
├── domain/       # エンティティ・enum
├── repository/   # Spring Data JPA
├── service/      # 業務ロジック・トランザクション境界
├── web/          # Controller / DTO / エラーハンドラ
└── support/      # 共通ユーティリティ
```

- Controller は入力変換とレスポンス返却に集中させ、業務ロジックは Service に置く。
- Service にトランザクション境界と検証を集約する。
- 一覧 API は既存の `ProjectResponse` など DTO を返し、エンティティを直接公開しない。
- API は `/api/v1` 配下。エラーは既存の共通形式に従う。

### フロントエンド

```text
frontend/src/
├── pages/        # 画面
├── components/   # 共通 UI（workspace/ を含む）
├── data/         # ガイド・デモデータ
└── api.ts / types.ts / utils.ts
```

- 画面追加時は `pages/`、共通部品は `components/` に置く。
- ルーティングは HashRouter。静的配信でリロードしても 404 にならない構成を維持する。
- API 未起動時はデモデータへフォールバックする既存挙動を壊さない。

## テストと検証

変更後は、影響範囲に応じて以下を実行する。

### バックエンド

```bash
./mvnw test                                   # 単体・結合テスト（e2e タグは除外）
./mvnw test -Dsurefire.excludedGroups=        # Java の Playwright E2E も実行
```

- Testcontainers のため Docker が必要。
- Java E2E は Playwright の API リクエストのみで、ブラウザ install は不要。
- Windows で見られる Testcontainers の `ClosedChannelException` ログは既知の警告で、失敗でなければ無視してよい。

### フロントエンド

```bash
cd frontend
npm ci
npm run lint
npm run typecheck
npm run build
npm run test:e2e
```

- Playwright は `frontend/e2e/` にあり、`installApiMock` で `/api/v1/**` をモックするためバックエンド不要。
- 画面を変更したら、対応する `*.spec.ts` を更新または追加する。
- E2E を追加したら `npm run test:e2e` で必ず全件通す。
- 初回のみ Chromium が必要: `npx playwright install chromium`。

### CI

`.github/workflows/ci.yml` で以下を実行する。変更時は両ジョブが通る前提を維持する。

- `junit`: `./mvnw -B test -Dsurefire.excludedGroups=`（Java E2E 含む）
- `playwright`: lint / typecheck / build / `npm run test:e2e`（失敗時レポートを artifact 保存）

## 作業ルール

- 変更は依頼された範囲に限定し、無関係な整形・リファクタリングを混ぜない。
- 既存の命名・構成・文言・CSS の流儀に合わせる。
- 新しいライブラリを追加する前に、既存の依存関係で代替できないか確認する。
- 秘密情報・API キー・認証情報をコードやコミットに含めない。
- 明示的に依頼されない限り、コミット・push・PR 作成は行わない。
- テストが失敗した状態で完了報告しない。失敗が環境起因の場合は、その旨と再現方法を明記する。
