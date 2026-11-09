package com.example.uzu;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * テストプレイ・審査・ガイド・バックアップAPIの業務フローを検証する結合テストです。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("補助 API の結合テスト")
class SupportingApiIntegrationTest {

    /** テスト用のPostgreSQLコンテナ。 */
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");

    /**
     * Testcontainersの接続情報をSpringのデータソース設定へ登録します。
     *
     * @param registry 動的プロパティの登録先
     */
    @DynamicPropertySource
    static void datasourceProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    /** API呼び出しに使うRestTemplate。 */
    @Autowired
    private TestRestTemplate rest;

    /**
     * テストプレイ記録の作成・一覧・更新・削除が一連で動作することを検証します。
     */
    @Test
    @DisplayName("テストプレイ記録: 作成・一覧・更新・削除が一連で動作する")
    void playtestFlow_supportsCreateListUpdateAndDelete() {
        final UUID projectId = createProject("テストプレイ記録の作品");
        final String path = "/api/v1/projects/" + projectId + "/playtests";

        final JsonNode created = send(path, HttpMethod.POST, mapOf(
                "playedAt", "2026-11-01",
                "platform", "UZU",
                "participantCount", 4,
                "versionNote", "v1.0",
                "questionnaireCollected", true,
                "questionnaireUrl", "https://example.com/form",
                "findings", "導入が長い",
                "memo", "初回"), HttpStatus.CREATED);
        final String playtestId = created.get("id").asText();
        assertThat(created.get("projectId").asText()).isEqualTo(projectId.toString());
        assertThat(created.get("playedAt").asText()).isEqualTo("2026-11-01");
        assertThat(created.get("platform").asText()).isEqualTo("UZU");
        assertThat(created.get("participantCount").asInt()).isEqualTo(4);
        assertThat(created.get("questionnaireCollected").asBoolean()).isTrue();
        assertThat(created.get("findings").asText()).isEqualTo("導入が長い");
        assertThat(created.get("createdAt").asText()).endsWith("+09:00");

        assertThat(get(path, HttpStatus.OK)).hasSize(1);

        final JsonNode updated = send(path + "/" + playtestId, HttpMethod.PATCH, mapOf(
                "platform", "OTHER",
                "participantCount", 5,
                "questionnaireCollected", false,
                "findings", "改善点を反映",
                "memo", "更新済み"), HttpStatus.OK);
        assertThat(updated.get("platform").asText()).isEqualTo("OTHER");
        assertThat(updated.get("participantCount").asInt()).isEqualTo(5);
        assertThat(updated.get("questionnaireCollected").asBoolean()).isFalse();
        assertThat(updated.get("memo").asText()).isEqualTo("更新済み");

        send(path + "/" + playtestId, HttpMethod.DELETE, null, HttpStatus.NO_CONTENT);
        assertThat(get(path, HttpStatus.OK)).isEmpty();
    }

    /**
     * テストプレイ記録の必須入力と存在しない作品・記録へのアクセスを検証します。
     */
    @Test
    @DisplayName("テストプレイ記録: 入力エラーと存在しないリソースを拒否する")
    void playtestFlow_rejectsInvalidRequests() {
        final UUID projectId = createProject("テストプレイ検証の作品");
        final String path = "/api/v1/projects/" + projectId + "/playtests";

        final JsonNode validationError = send(path, HttpMethod.POST, mapOf(), HttpStatus.BAD_REQUEST);
        assertThat(validationError.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");

        final JsonNode created = send(path, HttpMethod.POST, mapOf(
                "playedAt", "2026-11-02",
                "platform", "UZU"), HttpStatus.CREATED);
        final String playtestId = created.get("id").asText();

        final UUID otherProjectId = createProject("別作品");
        final JsonNode wrongProject = send(
                "/api/v1/projects/" + otherProjectId + "/playtests/" + playtestId,
                HttpMethod.PATCH, mapOf("memo", "別作品から更新"), HttpStatus.NOT_FOUND);
        assertThat(wrongProject.get("error").get("code").asText()).isEqualTo("NOT_FOUND");

        final JsonNode missingProject = send(
                "/api/v1/projects/" + UUID.randomUUID() + "/playtests",
                HttpMethod.POST, mapOf("playedAt", "2026-11-02", "platform", "UZU"), HttpStatus.NOT_FOUND);
        assertThat(missingProject.get("error").get("code").asText()).isEqualTo("NOT_FOUND");
    }

    /**
     * 審査記録と審査指摘事項の作成・更新・一覧・削除が一連で動作することを検証します。
     */
    @Test
    @DisplayName("審査: 審査記録と指摘事項の一連の操作が動作する")
    void reviewFlow_supportsRecordAndIssueLifecycle() {
        final UUID projectId = createProject("審査フローの作品");
        final String reviewsPath = "/api/v1/projects/" + projectId + "/reviews";

        final JsonNode review = send(reviewsPath, HttpMethod.POST, mapOf(
                "reviewType", "PAID",
                "submittedAt", "2026-11-02",
                "status", "SUBMITTED",
                "result", "提出済み",
                "memo", "初回提出"), HttpStatus.CREATED);
        final String reviewId = review.get("id").asText();
        assertThat(review.get("reviewType").asText()).isEqualTo("PAID");
        assertThat(review.get("submittedAt").asText()).isEqualTo("2026-11-02");
        assertThat(review.get("status").asText()).isEqualTo("SUBMITTED");
        assertThat(review.get("memo").asText()).isEqualTo("初回提出");

        assertThat(get(reviewsPath, HttpStatus.OK)).hasSize(1);

        final JsonNode updatedReview = send(reviewsPath + "/" + reviewId, HttpMethod.PATCH, mapOf(
                "reviewType", "FREE",
                "submittedAt", "2026-11-03",
                "status", "PASSED",
                "result", "合格",
                "memo", "審査完了"), HttpStatus.OK);
        assertThat(updatedReview.get("reviewType").asText()).isEqualTo("FREE");
        assertThat(updatedReview.get("status").asText()).isEqualTo("PASSED");
        assertThat(updatedReview.get("result").asText()).isEqualTo("合格");

        final String issuesPath = reviewsPath + "/" + reviewId + "/issues";
        assertThat(get(issuesPath, HttpStatus.OK)).isEmpty();

        final JsonNode issue = send(issuesPath, HttpMethod.POST, mapOf(
                "content", "  表記揺れ  ",
                "status", "IN_PROGRESS",
                "responseMemo", "修正中"), HttpStatus.CREATED);
        final String issueId = issue.get("id").asText();
        assertThat(issue.get("content").asText()).isEqualTo("表記揺れ");
        assertThat(issue.get("status").asText()).isEqualTo("IN_PROGRESS");
        assertThat(issue.get("responseMemo").asText()).isEqualTo("修正中");
        assertThat(get(issuesPath, HttpStatus.OK)).hasSize(1);

        final JsonNode updatedIssue = send(issuesPath + "/" + issueId, HttpMethod.PATCH, mapOf(
                "content", "  修正済みの指摘  ",
                "status", "RESOLVED",
                "responseMemo", "対応済み"), HttpStatus.OK);
        assertThat(updatedIssue.get("content").asText()).isEqualTo("修正済みの指摘");
        assertThat(updatedIssue.get("status").asText()).isEqualTo("RESOLVED");
        assertThat(updatedIssue.get("responseMemo").asText()).isEqualTo("対応済み");

        send(issuesPath + "/" + issueId, HttpMethod.DELETE, null, HttpStatus.NO_CONTENT);
        assertThat(get(issuesPath, HttpStatus.OK)).isEmpty();

        final JsonNode missingIssue = send(
                issuesPath + "/" + UUID.randomUUID(), HttpMethod.DELETE, null, HttpStatus.NOT_FOUND);
        assertThat(missingIssue.get("error").get("code").asText()).isEqualTo("NOT_FOUND");
    }

    /**
     * 審査記録と審査指摘事項の入力エラー・存在しないリソースを検証します。
     */
    @Test
    @DisplayName("審査: 入力エラーと存在しないリソースを拒否する")
    void reviewFlow_rejectsInvalidRequests() {
        final UUID projectId = createProject("審査検証の作品");
        final String reviewsPath = "/api/v1/projects/" + projectId + "/reviews";

        final JsonNode missingType = send(reviewsPath, HttpMethod.POST, mapOf("memo", "種別なし"),
                HttpStatus.BAD_REQUEST);
        assertThat(missingType.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");

        final JsonNode review = send(reviewsPath, HttpMethod.POST, mapOf("reviewType", "FREE"),
                HttpStatus.CREATED);
        final String reviewId = review.get("id").asText();
        final String issuesPath = reviewsPath + "/" + reviewId + "/issues";

        final JsonNode blankContent = send(issuesPath, HttpMethod.POST, mapOf("content", "   "),
                HttpStatus.BAD_REQUEST);
        assertThat(blankContent.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");

        final JsonNode missingReview = send(
                reviewsPath + "/" + UUID.randomUUID() + "/issues",
                HttpMethod.POST, mapOf("content", "指摘"), HttpStatus.NOT_FOUND);
        assertThat(missingReview.get("error").get("code").asText()).isEqualTo("NOT_FOUND");
    }

    /**
     * ガイドのカテゴリ・一覧・カテゴリ絞り込み・キーワード検索・詳細取得を検証します。
     */
    @Test
    @DisplayName("ガイド: カテゴリ・検索・詳細取得が動作する")
    void guideEndpoints_supportCategorySearchAndDetail() {
        final JsonNode categories = get("/api/v1/guide-categories", HttpStatus.OK);
        assertThat(categories).hasSize(6);
        boolean hasPlaytestCategory = false;
        for (final JsonNode category : categories) {
            if ("PLAYTEST".equals(category.get("code").asText())) {
                hasPlaytestCategory = true;
                assertThat(category.get("label").asText()).isEqualTo("テストプレイ");
            }
        }
        assertThat(hasPlaytestCategory).isTrue();

        final JsonNode allGuides = get("/api/v1/guides", HttpStatus.OK);
        assertThat(allGuides).hasSize(8);
        final String guideId = allGuides.get(0).get("id").asText();

        final JsonNode byCategory = get("/api/v1/guides?category=PLAYTEST", HttpStatus.OK);
        assertThat(byCategory).hasSize(1);
        assertThat(byCategory.get(0).get("title").asText()).isEqualTo("テストプレイ");

        final JsonNode byJapaneseCategory = get(
                "/api/v1/guides?category=" + encode("テストプレイ"), HttpStatus.OK);
        assertThat(byJapaneseCategory).hasSize(1);

        final JsonNode byKeyword = get("/api/v1/guides?keyword=" + encode("審査"), HttpStatus.OK);
        assertThat(byKeyword).isNotEmpty();

        final JsonNode invalidCategory = get("/api/v1/guides?category=UNKNOWN", HttpStatus.BAD_REQUEST);
        assertThat(invalidCategory.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");

        final JsonNode detail = get("/api/v1/guides/" + guideId, HttpStatus.OK);
        assertThat(detail.get("id").asText()).isEqualTo(guideId);
        assertThat(detail.get("createdAt").asText()).endsWith("+09:00");

        final JsonNode missingGuide = get("/api/v1/guides/" + UUID.randomUUID(), HttpStatus.NOT_FOUND);
        assertThat(missingGuide.get("error").get("code").asText()).isEqualTo("NOT_FOUND");
    }

    /**
     * バックアップのエクスポートと、空・不正な復元データの扱いを検証します。
     */
    @Test
    @DisplayName("バックアップ: エクスポートと空データの復元が動作する")
    void backupEndpoints_exportAndRestoreEmptyPayload() {
        final UUID projectId = createProject("バックアップ対象の作品");

        final JsonNode backup = get("/api/v1/backup", HttpStatus.OK);
        assertThat(backup.has("projects")).isTrue();
        assertThat(backup.has("checklistItems")).isTrue();
        assertThat(backup.has("playtestRecords")).isTrue();
        assertThat(backup.has("reviewRecords")).isTrue();
        assertThat(backup.has("reviewIssues")).isTrue();
        assertThat(backup.has("guideArticles")).isTrue();
        assertThat(containsId(backup.get("projects"), projectId)).isTrue();

        final JsonNode restored = send("/api/v1/restore", HttpMethod.POST, mapOf(
                "projects", java.util.List.of(),
                "checklistItems", "一覧ではない値"), HttpStatus.OK);
        assertThat(restored.has("projects")).isTrue();
        assertThat(containsId(restored.get("projects"), projectId)).isTrue();
    }

    /**
     * 作品を作成し、そのIDを返します。
     *
     * @param title 作品タイトル
     * @return 作成した作品ID
     */
    private UUID createProject(final String title) {
        final JsonNode created = send("/api/v1/projects", HttpMethod.POST,
                mapOf("title", title, "releaseType", "PAID"), HttpStatus.CREATED);
        return UUID.fromString(created.get("id").asText());
    }

    /**
     * 文字列をURLエンコードします。
     *
     * @param value エンコードする値
     * @return エンコード後の値
     */
    private String encode(final String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    /**
     * GETリクエストを送信し、期待するステータスのJSONを返します。
     *
     * @param path リクエストパス
     * @param expected 期待するHTTPステータス
     * @return レスポンスJSON
     */
    private JsonNode get(final String path, final HttpStatus expected) {
        return send(path, HttpMethod.GET, null, expected);
    }

    /**
     * 任意のHTTPメソッドでリクエストを送信し、ステータスを検証してJSONを返します。
     *
     * @param path リクエストパス
     * @param method HTTPメソッド
     * @param body リクエスト本文
     * @param expected 期待するHTTPステータス
     * @return レスポンスJSON
     */
    private JsonNode send(final String path, final HttpMethod method, final Object body,
                          final HttpStatus expected) {
        final ResponseEntity<JsonNode> response = rest.exchange(
                URI.create(path), method, entity(body), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(expected);
        return response.getBody();
    }

    /**
     * JSONリクエスト用のHTTPエンティティを生成します。
     *
     * @param body リクエスト本文
     * @return Content-TypeをJSONに設定したHTTPエンティティ
     */
    private HttpEntity<Object> entity(final Object body) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * キーと値の組からリクエスト本文を生成します。
     *
     * @param pairs キーと値の組
     * @return リクエスト本文
     */
    private Map<String, Object> mapOf(final Object... pairs) {
        final Map<String, Object> body = new LinkedHashMap<>();
        for (int index = 0; index < pairs.length; index += 2) {
            body.put((String) pairs[index], pairs[index + 1]);
        }
        return body;
    }

    /**
     * JSON配列に指定した作品IDが含まれるかどうかを判定します。
     *
     * @param array 検索対象のJSON配列
     * @param id 検索する作品ID
     * @return 含まれる場合はtrue
     */
    private boolean containsId(final JsonNode array, final UUID id) {
        for (final JsonNode node : array) {
            if (node.get("id").asText().equals(id.toString())) {
                return true;
            }
        }
        return false;
    }
}
