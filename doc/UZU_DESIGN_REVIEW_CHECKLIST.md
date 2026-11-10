# UZU Studio 設計レビュー チェックリスト

2台のAIレビュー（A: バックエンド/ドメイン、B: フロント/UX/テスト）と相互反論の結果を、重要度別・カテゴリ別の実行チェックリストに整理したものです。

## 重要度

| 重要度 | 意味 | 目安 |
| --- | --- | --- |
| **S** | リリース前必須。データ損失・契約破綻・主要導線の不成立につながる | Phase 0（今すぐ） |
| **A** | 早期に設計修正。放置すると実装が硬直化する | Phase 1（次フェーズ） |
| **B** | 品質改善。機能追加や運用改善と合わせて対応可 | Phase 2（余力次第） |

- `[ ]` 未対応
- `[x]` 対応済み

## サマリー

| カテゴリ | S | A | B | 合計 |
| --- | ---: | ---: | ---: | ---: |
| データ保全・バックアップ | 6 | 2 | 1 | 9 |
| ドメイン・状態モデル | 6 | 3 | 2 | 11 |
| API・契約 | 2 | 4 | 4 | 10 |
| 永続化・DB整合性 | 0 | 4 | 1 | 5 |
| フロントエンド状態・UX | 2 | 1 | 5 | 8 |
| セキュリティ・入力境界 | 0 | 2 | 0 | 2 |
| テスト・CI・ビルド | 2 | 2 | 5 | 9 |
| アクセシビリティ | 0 | 0 | 5 | 5 |
| ドキュメント | 0 | 0 | 4 | 4 |
| **合計** | **18** | **18** | **27** | **63** |

---

## 1. データ保全・バックアップ（BKP）

- [ ] **BKP-01 [S]** バックアップ文書に `schemaVersion` / `exportedAt` / アプリバージョンを持たせる
  - 根拠: `src/main/java/com/example/uzu/service/BackupService.java:98-107`
  - 完了条件: エクスポートJSONだけを見て互換性を判定でき、未知の版は復元前に拒否できる

- [ ] **BKP-02 [S]** 復元時に必須キー・型・件数上限・FK整合性を全件検証し、不正時は全体を失敗させる
  - 根拠: `BackupService.java:139-156`（キー欠落・型不正を黙ってスキップ）
  - 完了条件: 1件でも不正ならDBが変更されず、エラー内容を返す

- [ ] **BKP-03 [S]** 復元前に自動バックアップを取得し、UIで対象件数と置換範囲を確認させる
  - 根拠: `frontend/src/pages/Settings.tsx:130-217`、`BackupService.java:115-128`
  - 完了条件: 復元失敗時も事前バックアップから戻せる。確認なしの破壊操作ができない

- [ ] **BKP-04 [S]** 復元を「同一環境の全置換」として単一トランザクションで実行する（または明示的なマージモードを分ける）
  - 根拠: `BackupService.java:115-128`（`saveAll`の追記型）
  - 完了条件: バックアップ時点に存在したデータだけが残り、削除済みデータが復活しない

- [ ] **BKP-05 [S]** ID・`createdAt`・`updatedAt` の復元ポリシーを決めて文書化する
  - 根拠: `src/main/java/com/example/uzu/domain/AuditedEntity.java:56-69`
  - 完了条件: IDは保持、`updatedAt`は現在時刻になる等の挙動が仕様とテストに一致する

- [ ] **BKP-06 [S]** セクションメモ・最終確認日をバックアップに含める
  - 根拠: `frontend/src/components/workspace/SectionMemoPanel.tsx:8-23`（localStorageのみ）、`BackupService.java:98-107`
  - 完了条件: エクスポート→別ブラウザ/再インストール→インポートでメモと確認日が復元される

- [ ] **BKP-07 [A]** エクスポートを一貫したスナップショットにし、チェックサムと復元ドライランを用意する
  - 根拠: `BackupService.java:98-107`（非トランザクション）
  - 完了条件: エクスポート中に更新が入ってもテーブル間で不整合にならない。復元前に件数差分を確認できる

- [ ] **BKP-08 [A]** ローカルメモ削除など破壊的操作に確認と取り消し不能の明示を入れる
  - 根拠: `frontend/src/pages/Settings.tsx:75-79`
  - 完了条件: 確認ダイアログなしに全作品分のメモが消えない

- [ ] **BKP-09 [B]** 別環境への復元は未対応であることを明示し、必要になった場合のID再割当方針を決める
  - 根拠: `BackupService.java:115-128`
  - 完了条件: README/UIに「同一環境向け」と記載され、誤用を防げる

---

## 2. ドメイン・状態モデル（DOM）

- [ ] **DOM-01 [S]** `ChecklistItem` に `sectionKey` を永続化する
  - 根拠: `src/main/java/com/example/uzu/domain/ChecklistItem.java:15-59`、`frontend/src/utils.ts:36-67`
  - 完了条件: 項目名を変更してもセクション進捗が変わらない

- [ ] **DOM-02 [S]** `uzuTemplate` / `templateVersion` とバージョン付き標準項目定義を導入する
  - 根拠: `src/main/java/com/example/uzu/service/StandardChecklist.java:18-53`、`src/main/resources/db/migration/V3__seed_vampire_mansion_scenario.sql:24-163`
  - 完了条件: 標準項目の改版が新規作品にだけ適用され、既存作品は作成時の版を保持する

- [ ] **DOM-03 [S]** `ProjectSectionState(projectId, sectionKey, memo, verifiedAt)` をサーバー保存する
  - 根拠: `frontend/src/components/workspace/SectionMemoPanel.tsx:8-23`
  - 完了条件: 端末を変えてもメモと確認日が引き継がれ、バックアップ対象になる

- [ ] **DOM-04 [S]** 項目名からのセクション推測を廃止し、既存データは明示マッピングでバックフィルする
  - 根拠: `frontend/src/utils.ts:36-67`（先勝ち`find`）
  - 完了条件: 未マッピングと「項目なし」を区別でき、推測に依存しない

- [ ] **DOM-05 [S]** 公開準備・次アクションをサーバーの `ReadinessPolicy` / `NextActionPolicy` に集約する
  - 根拠: `frontend/src/components/workspace/readiness.ts:9-43`
  - 完了条件: クライアントは判定結果を表示するだけで、判定ロジックを持たない

- [ ] **DOM-06 [S]** テストプレイ・審査の実績をreadinessに接続する
  - 根拠: `frontend/src/pages/Dashboard.tsx:48-58` / `:73-85`（項目名一致）、`src/main/java/com/example/uzu/web/dto/ProjectResponse.java:87-95`
  - 完了条件: 「テスト3回・UZU上1回」「審査指摘の未対応件数」が記録データから算出される

- [ ] **DOM-07 [A]** チェックリスト・テストプレイ・審査の更新時に `Project.updatedAt` を更新する
  - 根拠: `src/main/java/com/example/uzu/service/ChecklistService.java:98-120`、`frontend/src/pages/Dashboard.tsx:20-24`
  - 完了条件: 作業した作品が「続きから」の先頭に来る

- [ ] **DOM-08 [A]** アーカイブ済み作品を全子リソースAPIで読み取り専用にする
  - 根拠: `ProjectService.java:120-158`、`ChecklistService.java:98-120`
  - 完了条件: 直接URL・API経由でも更新できず、UIは読み取り専用表示になる

- [ ] **DOM-09 [A]** `releaseType` 変更時の標準項目ポリシーを決める
  - 根拠: `ProjectService.java:135-137`
  - 完了条件: FREE→PAIDは追加候補を提示し、PAID→FREEでもユーザー記録を自動削除しない

- [ ] **DOM-10 [B]** 審査モデルの `reviewType` / `result` の重複を見直す
  - 根拠: `src/main/java/com/example/uzu/domain/ReviewRecord.java:24-29` / `:44-48`、`ReviewStatus.java:6-11`
  - 完了条件: 公開形態・審査種別・合否の意味が一意になり、画面/APIで同じ言葉を使う

- [ ] **DOM-11 [B]** 44セクションの段階的開示（ピン留め・最近使った・大項目折りたたみ・検索・未設定バッジ）を検討する
  - 根拠: `frontend/src/components/SectionNav.tsx:18-23`、`frontend/src/data/uzu.ts`
  - 完了条件: 空セクションが常態化しても目的のセクションに到達できる

---

## 3. API・契約（API）

- [ ] **API-01 [S]** 作品一覧API呼び出しで `archived` を明示的に渡す
  - 根拠: `frontend/src/api.ts:51-53`、`src/main/java/com/example/uzu/service/ProjectService.java:61-66`、`frontend/src/pages/Projects.tsx:18-23`
  - 完了条件: 「アーカイブを表示」で実際にアーカイブ作品が取得できる

- [ ] **API-02 [S]** ワークスペース集約API `GET /api/v1/projects/{id}/workspace` を設計・実装する
  - 根拠: `frontend/src/hooks/useProjectCollection.ts:14-22`、`doc/UZU_STUDIO_COMPANION_UI.md:622-626`
  - 完了条件: 作品・チェック・セクション状態・テスト集計・審査集計・readiness・nextActionを1リクエストで取得できる

- [ ] **API-03 [A]** 作品一覧を集約クエリ/プロジェクションにし、1+2NのN+1を解消する
  - 根拠: `ProjectService.java:206-210`、`useProjectCollection.ts:14-22`
  - 完了条件: 一覧取得のクエリ数が作品数に比例しない

- [ ] **API-04 [A]** PATCHの「未指定」と「nullでクリア」を区別する
  - 根拠: `ProjectService.java:123-155`、`ChecklistService.java:110-115`、`PlaytestService.java:119-133`
  - 完了条件: URLやメモをAPIからクリアでき、未指定項目は変更されない

- [ ] **API-05 [A]** テストプレイ等の入力にBean Validation（正値・最大長・必須）を追加する
  - 根拠: `src/main/java/com/example/uzu/web/dto/PlaytestRecordCreateRequest.java:28-48`
  - 完了条件: 不正入力が500ではなく400の共通エラー形式で返る

- [ ] **API-06 [A]** フロントのAPI境界でレスポンス検証・Abort・限定リトライ・エラー詳細表示を行う
  - 根拠: `frontend/src/api.ts:20-48`（`as T`のみ）、`:29-40`（detailsを捨てる）
  - 完了条件: 不正レスポンスを型キャストで通過させず、通信中断・再試行・項目別エラーを扱える

- [ ] **API-07 [B]** 作品一覧に `limit` / `offset` または `Pageable` を追加する
  - 根拠: `ProjectRepository.java:23` / `:33`
  - 完了条件: 件数増加時も一覧取得の応答とメモリ使用が線形に増えない

- [ ] **API-08 [B]** ガイド検索のカテゴリ条件落ちを修正し、ガイドの正本を一本化する
  - 根拠: `src/main/java/com/example/uzu/service/GuideService.java:159-165`、`frontend/src/pages/Guide.tsx:6` / `:22`
  - 完了条件: `category` と `keyword` の併用が効き、フロント静的データとDBの二重管理が解消される

- [ ] **API-09 [B]** サンプルデータと作品削除の扱いを整理する
  - 根拠: `V3__seed_vampire_mansion_scenario.sql:2-21`、`ProjectController.java:93-101`、`README.md`の「削除」記述
  - 完了条件: サンプルは開発プロファイル/`is_sample`に分離され、削除APIの有無とREADMEが一致する

- [ ] **API-10 [B]** APIはあるがUI導線がないフローを実装またはPhase外と明記する
  - 根拠: `frontend/src/App.tsx:27-34`、`frontend/src/api.ts`（playtest/review/archive/deleteなし）
  - 完了条件: テストプレイ・審査・アーカイブ・期限・並べ替えの扱いが要件と一致する

---

## 4. 永続化・DB整合性（DB）

- [ ] **DB-01 [A]** 監査エンティティに `@Version` を追加し、同時更新のlost updateを防ぐ
  - 根拠: `src/main/java/com/example/uzu/domain/AuditedEntity.java:22-36`
  - 完了条件: 競合時に後勝ちで上書きされず、409等で検知できる

- [ ] **DB-02 [A]** enum・件数・正値に対するCHECK制約を追加する
  - 根拠: `src/main/resources/db/migration/V1__init.sql:22-35`、`V2__add_uzu_records.sql`
  - 完了条件: 直接SQLや復元経由でも不正値が入らない

- [ ] **DB-03 [A]** `display_order` の一意性を保証し、並べ替え専用APIを用意する
  - 根拠: `ChecklistService.java:74-77`（`count + 1`）
  - 完了条件: 削除後の追加でも重複せず、一括並べ替えが原子的に反映される

- [ ] **DB-04 [A]** 日時を `TIMESTAMPTZ` に統一するか、`TIMESTAMP` + Asia/Tokyo固定を明示する
  - 根拠: `V1__init.sql:15-16`、`V3__seed_vampire_mansion_scenario.sql:20-21`、`docker-compose.yml:5-8`、`application.yml:9-20`
  - 完了条件: サンプル・API・画面で9時間ずれが発生しない

- [ ] **DB-05 [B]** 外部キーの削除方針（CASCADE/RESTRICT）と検索用インデックスを整理する
  - 根拠: `V1__init.sql`、`V2__add_uzu_records.sql`
  - 完了条件: 親削除時の孤児データが発生せず、一覧・検索が全表走査にならない

---

## 5. フロントエンド状態・UX（FE）

- [ ] **FE-01 [S]** 接続状態を `connected / partial / demo` の明示状態にし、実データを黙ってデモに置換しない
  - 根拠: `frontend/src/hooks/useProjectCollection.ts:14-41`、`frontend/src/api.ts:110-112`
  - 完了条件: 部分障害時は実作品を残して「進捗不明」を表示する

- [ ] **FE-02 [S]** デモ中の書き込みが保存されないことを画面単位で明示する
  - 根拠: `frontend/src/pages/Workspace.tsx:65-79` / `:120-166`、`frontend/src/pages/Compact.tsx:26-32` / `:68-87`
  - 完了条件: デモでチェックやメモを変更しても「保存済み」と誤認させない

- [ ] **FE-03 [A]** 楽観更新のロールバックを配列全体ではなく項目ID単位にする
  - 根拠: `Workspace.tsx:115-157`、`Compact.tsx:80-118`
  - 完了条件: 1項目の保存失敗で、同時に更新した別項目まで巻き戻らない

- [ ] **FE-04 [B]** 保存中・保存済み・保存失敗・再試行を項目単位で表示する
  - 根拠: `frontend/src/pages/Workspace.tsx:106-113`、`ChecklistRow.tsx:71-80`、`SectionMemoPanel.tsx:13-16`
  - 完了条件: トーストに依存せず、失敗した入力が保持され再試行できる

- [ ] **FE-05 [B]** localStorageのキーにスキーマ版を付け、画面状態・設定の永続化を要件に合わせる
  - 根拠: `frontend/src/utils.ts:102-117`、`SectionNav.tsx:18-23`、`Settings.tsx`
  - 完了条件: 形式変更時に移行でき、グループ開閉・表示設定が再訪時に復元される

- [ ] **FE-06 [B]** 空状態・ローディング・部分エラー・再読み込み導線を画面ごとに整理する
  - 根拠: `frontend/src/components/Loading.tsx`、`useProjectCollection.ts:14-41`
  - 完了条件: 通信失敗時に「作品がない」と誤認させない

- [ ] **FE-07 [B]** ガイドUIを単一の正本に接続し、確認日・セクション導線を実装する
  - 根拠: `Guide.tsx:20-39` / `:243`、`types.ts:72-86`
  - 完了条件: ガイド更新が一箇所で完結し、機能から該当セクションへ遷移できる（UI-027はShouldとして扱う）

- [ ] **FE-08 [B]** アーカイブ作品の直接URLアクセス時に読み取り専用UIを表示する
  - 根拠: `Workspace.tsx:42-48` / `:220-284`、`Compact.tsx`
  - 完了条件: アーカイブ済みでも編集フォーム・保存ボタンが無効化される

---

## 6. セキュリティ・入力境界（SEC）

- [ ] **SEC-01 [A]** 外部URLは `http` / `https` のみ許可し、`href` 利用前に検証する
  - 根拠: `Project.java:77-84`、`PlaytestRecord.java:58-59`、`WorkspaceTopbar.tsx:46`、`Compact.tsx:138`
  - 完了条件: `javascript:` 等がリンクとして描画されない

- [ ] **SEC-02 [A]** 復元エンドポイントをローカル限定にし、Origin/Host確認と確認トークンを入れる
  - 根拠: `src/main/java/com/example/uzu/web/BackupController.java:51-54`
  - 完了条件: 外部ページや別ホストから破壊的な復元を実行できない

---

## 7. テスト・CI・ビルド（QA）

- [ ] **QA-01 [S]** Playwrightモックを実API契約に合わせる（`archived`フィルタ、標準チェックリスト件数など）
  - 根拠: `frontend/e2e/fixtures.ts:116-118` / `:120-140`
  - 完了条件: 本番で壊れている挙動をE2Eが緑にしない

- [ ] **QA-02 [S]** 作成→チェック→アーカイブ→バックアップ→復元の契約テストを追加する
  - 根拠: `src/test/java/com/example/uzu/e2e/ProjectApiE2ETest.java:67-106`
  - 完了条件: ID・件数・アーカイブ状態・メモがラウンドトリップで一致する

- [ ] **QA-03 [A]** Playtest / Review / Backup のサービス・APIテストを追加する
  - 根拠: `src/test/java/com/example/uzu/` に該当テストなし
  - 完了条件: 復元の版不一致・キー欠落・部分失敗・審査指摘のCRUDが検証される

- [ ] **QA-04 [A]** フロントのフック/コンポーネントテストを追加する
  - 根拠: `frontend/package.json:6-12`（E2Eのみ）
  - 完了条件: 部分失敗・デモ切替・楽観更新ロールバック・直接URLを単体で検証できる

- [ ] **QA-05 [B]** E2Eを開発サーバーではなくビルド成果物（`vite preview`）に対して実行する
  - 根拠: `frontend/playwright.config.ts:23-27`
  - 完了条件: 本番配信に近い成果物でE2Eが通る

- [ ] **QA-06 [B]** フロントビルドをMaven/CIに統合し、UI込みWARの生成を再現可能にする
  - 根拠: `frontend/vite.config.ts:15-18`、`README.md`のデプロイ手順
  - 完了条件: `./mvnw clean package` または明示したリリース手順だけでUI込み成果物が作れる

- [ ] **QA-07 [B]** CIの診断性を上げる（JUnitレポート保存、ブラウザキャッシュ、タイムアウト）
  - 根拠: `.github/workflows/ci.yml:21-22` / `:42-43`
  - 完了条件: 失敗時にテストレポートとスクリーンショットがartifactから確認できる

- [ ] **QA-08 [B]** E2EのセレクタをCSSクラス依存からrole/label中心に変更する
  - 根拠: `dashboard.spec.ts:36-41`、`projects.spec.ts:36`
  - 完了条件: 見た目のクラス変更でテストが壊れない

- [ ] **QA-09 [B]** モックE2Eと実APIスモークE2Eを分離し、フレークを可視化する
  - 根拠: `frontend/playwright.config.ts:9-10`
  - 完了条件: UX網羅と実API結合を別ジョブで実行し、`retries`に依存しない

---

## 8. アクセシビリティ（A11Y）

- [ ] **A11Y-01 [B]** Modalに初期フォーカス・フォーカストラップ・Escape・フォーカス復帰を実装する
  - 根拠: `frontend/src/components/Modal.tsx:17-51`
  - 完了条件: キーボードだけで開閉でき、背後の要素にフォーカスが漏れない

- [ ] **A11Y-02 [B]** 折りたたみ・タブに `aria-expanded` / `role=tablist` 等を付与する
  - 根拠: `AppShell.tsx:88-112`、`SectionNav.tsx:69`、`Compact.tsx:267-284`
  - 完了条件: スクリーンリーダーで開閉状態とタブ構造が伝わる

- [ ] **A11Y-03 [B]** ProgressBarにラベルと現在値を付与する
  - 根拠: `frontend/src/components/ProgressBar.tsx:12-18`
  - 完了条件: 進捗が読み上げられる

- [ ] **A11Y-04 [B]** グローバルな `:focus-visible`、`prefers-reduced-motion`、スキップリンクを追加する
  - 根拠: `frontend/src/styles.css`
  - 完了条件: キーボード操作でフォーカスが見え、アニメーション抑制設定が効く

- [ ] **A11Y-05 [B]** 色だけで状態を伝えない（コントラスト・アイコン・テキスト併用）
  - 根拠: ステータスバッジ・進捗表示全般
  - 完了条件: 色覚特性やモノクロでも状態が判別できる

---

## 9. ドキュメント（DOC）

- [ ] **DOC-01 [B]** 設計書の「実装済みAPI」と「予定API」を分離する
  - 根拠: `doc/UZU_STUDIO_COMPANION_UI.md:622-626` / `:743`
  - 完了条件: 存在しないAPIを実装済みと誤認しない

- [ ] **DOC-02 [B]** READMEのテスト・デプロイ・削除手順を実装に合わせる
  - 根拠: `README.md`のテスト/デプロイ節、`ProjectController.java:93-101`
  - 完了条件: 記載コマンドだけで再現でき、存在しない操作を案内しない

- [ ] **DOC-03 [B]** 重要な設計判断をADRとして残す
  - 根拠: バックアップ意味論、セクションモデル、`releaseType`変更、タイムゾーン
  - 完了条件: なぜその方式にしたかを後から追跡できる

- [ ] **DOC-04 [B]** 要件のMustと実装フェーズの食い違いを解消する
  - 根拠: `doc/UZU_CREATOR_REQUIREMENTS.md`、`doc/UZU_STUDIO_COMPANION_UI.md`
  - 完了条件: 未実装のMustは「除外」か「Phase」のどちらかに明記される

---

## 推奨実行順

### Phase 0: リリース前（S 18件）
1. `BKP-01`〜`BKP-06`: 復元契約とメモ保全
2. `DOM-01`〜`DOM-06`: セクション状態とreadinessの正本化
3. `API-01`〜`API-02`: アーカイブ取得とワークスペース集約
4. `FE-01`〜`FE-02`: デモ/部分障害の明示
5. `QA-01`〜`QA-02`: 契約乖離を検出するテスト

### Phase 1: 設計基盤（A 18件）
- `DOM-07`〜`DOM-09`、`API-03`〜`API-06`、`DB-01`〜`DB-04`、`BKP-07`〜`BKP-08`、`FE-03`、`SEC-01`〜`SEC-02`、`QA-03`〜`QA-04`

### Phase 2: 品質・拡張（B 27件）
- 一覧ページング、ガイド一本化、UI未実装フロー、アクセシビリティ、CI診断性、ドキュメント整備

## 依存関係の注意

- `DOM-01` / `DOM-02` / `DOM-03` が完了するまで `API-02` の集約レスポンスは確定しない
- `BKP-01` / `BKP-02` が完了するまで `BKP-04` の全置換を実装しない
- `API-01` が完了するまで `FE-08` のアーカイブUIは検証できない
- `QA-01` が完了するまで `QA-02` のE2Eは実契約を保証しない
