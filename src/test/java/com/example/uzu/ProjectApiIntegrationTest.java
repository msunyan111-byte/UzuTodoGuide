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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 実際のPostgreSQLとSpring Bootを起動し、作品APIの一連の業務フローを検証する結合テストです。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("作品 API の結合テスト")
class ProjectApiIntegrationTest {

    /** テスト用のPostgreSQLコンテナ。 */
    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");

    /**
     * Testcontainersの接続情報をSpringのデータソース設定へ登録します。
     *
     * @param registry 動的プロパティの登録先
     */
    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    /** API呼び出しに使うRestTemplate。 */
    @Autowired
    private TestRestTemplate rest;

    /**
     * 作品作成からチェック更新・項目追加・削除・アーカイブまでが一連で動作することを検証します。
     */
    @Test
    @DisplayName("作品作成からアーカイブまで: 標準チェックリストと進捗更新が一連で動作する")
    void createProject_createsStandardChecklistAndSupportsProgressFlow() {
        JsonNode created = send("/api/v1/projects", HttpMethod.POST, projectBody("夜明けの証言", "FREE"), HttpStatus.CREATED);
        UUID projectId = UUID.fromString(created.get("id").asText());
        assertThat(created.get("stage").asText()).isEqualTo("PLANNING");
        assertThat(created.get("releaseType").asText()).isEqualTo("FREE");
        assertThat(created.get("scoringRule").asText()).isEqualTo("TIE_ALLOWED");
        assertThat(created.get("archived").asBoolean()).isFalse();
        assertThat(created.get("checklistTotal").asLong()).isEqualTo(34);
        assertThat(created.get("checklistCompleted").asLong()).isZero();
        assertThat(created.get("createdAt").asText()).endsWith("+09:00");

        JsonNode checklist = get("/api/v1/projects/" + projectId + "/checklist", HttpStatus.OK);
        assertThat(checklist).hasSize(34);
        assertThat(checklist.get(0).get("displayOrder").asInt()).isEqualTo(1);
        assertThat(checklist.get(0).get("name").asText()).isEqualTo("【作品登録】作品の基本情報を登録する");
        assertThat(checklist.get(0).get("status").asText()).isEqualTo("NOT_STARTED");

        String itemId = checklist.get(0).get("id").asText();
        JsonNode done = send("/api/v1/projects/" + projectId + "/checklist/" + itemId,
                HttpMethod.PATCH, mapOf("status", "DONE", "memo", "確認済み"), HttpStatus.OK);
        assertThat(done.get("status").asText()).isEqualTo("DONE");
        assertThat(done.get("memo").asText()).isEqualTo("確認済み");

        JsonNode project = get("/api/v1/projects/" + projectId, HttpStatus.OK);
        assertThat(project.get("checklistCompleted").asLong()).isEqualTo(1);
        assertThat(project.get("checklistTotal").asLong()).isEqualTo(34);

        JsonNode updated = send("/api/v1/projects/" + projectId, HttpMethod.PATCH,
                mapOf("stage", "PLAYTEST", "scoringRule", "SOLE_TOP_REQUIRED", "memo", "テストプレイ中"), HttpStatus.OK);
        assertThat(updated.get("stage").asText()).isEqualTo("PLAYTEST");
        assertThat(updated.get("scoringRule").asText()).isEqualTo("SOLE_TOP_REQUIRED");
        assertThat(updated.get("memo").asText()).isEqualTo("テストプレイ中");

        JsonNode added = send("/api/v1/projects/" + projectId + "/checklist", HttpMethod.POST,
                mapOf("name", "追加の確認項目"), HttpStatus.CREATED);
        assertThat(added.get("displayOrder").asInt()).isEqualTo(35);
        String addedId = added.get("id").asText();

        send("/api/v1/projects/" + projectId + "/checklist/" + addedId, HttpMethod.DELETE, null, HttpStatus.NO_CONTENT);
        JsonNode afterDelete = get("/api/v1/projects/" + projectId + "/checklist", HttpStatus.OK);
        assertThat(afterDelete).hasSize(34);
        for (JsonNode item : afterDelete) {
            assertThat(item.get("id").asText()).isNotEqualTo(addedId);
        }

        JsonNode archived = send("/api/v1/projects/" + projectId + "/archive", HttpMethod.POST, null, HttpStatus.OK);
        assertThat(archived.get("archived").asBoolean()).isTrue();

        JsonNode activeList = get("/api/v1/projects", HttpStatus.OK);
        assertThat(containsId(activeList, projectId)).isFalse();

        JsonNode archivedList = get("/api/v1/projects?archived=true", HttpStatus.OK);
        assertThat(containsId(archivedList, projectId)).isTrue();

        JsonNode filteredByStage = get("/api/v1/projects?archived=true&status=PLAYTEST", HttpStatus.OK);
        assertThat(containsId(filteredByStage, projectId)).isTrue();
    }

    /**
     * 初期投入された吸血鬼の館シナリオが、想定どおりのTODOと補足情報を公開することを検証します。
     */
    @Test
    @DisplayName("初期データ: 吸血鬼の館のシナリオTODOを公開する")
    void seededVampireMansionProject_exposesScenarioTodos() {
        JsonNode projects = get("/api/v1/projects", HttpStatus.OK);
        JsonNode target = null;
        for (JsonNode project : projects) {
            if ("吸血鬼の館のマーダーミステリー".equals(project.get("title").asText())) {
                target = project;
                break;
            }
        }
        assertThat(target).isNotNull();
        assertThat(target.get("playerCount").asInt()).isEqualTo(4);
        assertThat(target.get("scoringRule").asText()).isEqualTo("TIE_ALLOWED");
        assertThat(target.get("checklistTotal").asLong()).isEqualTo(46);

        UUID projectId = UUID.fromString(target.get("id").asText());
        JsonNode checklist = get("/api/v1/projects/" + projectId + "/checklist", HttpStatus.OK);
        assertThat(checklist).hasSize(46);

        boolean hasVictimNatureIssue = false;
        boolean hasSimulationIssue = false;
        boolean hasScoringRule = false;
        for (JsonNode item : checklist) {
            String name = item.get("name").asText();
            if (name.contains("事件の性質と被害者")) {
                hasVictimNatureIssue = true;
                assertThat(item.get("description").asText()).contains("殺人未遂");
            }
            if (name.contains("各キャラクターの得点条件")) {
                hasScoringRule = true;
                assertThat(item.get("description").asText()).contains("同数最多も可");
            }
            if (name.contains("1000回シミュレーション")) {
                hasSimulationIssue = true;
                assertThat(item.get("memo").asText()).isEqualTo("未確定");
            }
        }
        assertThat(hasVictimNatureIssue).isTrue();
        assertThat(hasScoringRule).isTrue();
        assertThat(hasSimulationIssue).isTrue();
    }

    /**
     * 複数の作品を作成したとき、それぞれ独立した標準チェックリストが作成されることを検証します。
     */
    @Test
    @DisplayName("複数作品: それぞれ独立したチェックリストを作成する")
    void multipleProjects_canBeCreatedIndependently() {
        JsonNode free = send("/api/v1/projects", HttpMethod.POST, projectBody("別シナリオA", "FREE"), HttpStatus.CREATED);
        JsonNode paid = send("/api/v1/projects", HttpMethod.POST, projectBody("別シナリオB", "PAID"), HttpStatus.CREATED);

        UUID freeId = UUID.fromString(free.get("id").asText());
        UUID paidId = UUID.fromString(paid.get("id").asText());

        assertThat(free.get("checklistTotal").asLong()).isEqualTo(34);
        assertThat(paid.get("checklistTotal").asLong()).isEqualTo(36);
        assertThat(get("/api/v1/projects/" + freeId + "/checklist", HttpStatus.OK)).hasSize(34);
        assertThat(get("/api/v1/projects/" + paidId + "/checklist", HttpStatus.OK)).hasSize(36);
    }

    /**
     * 有料作品を作成したとき、有料公開専用のチェック項目が追加されることを検証します。
     */
    @Test
    @DisplayName("有料作品: 有料公開専用のチェックリスト項目を追加する")
    void createPaidProject_addsPaidSpecificChecklistItem() {
        JsonNode created = send("/api/v1/projects", HttpMethod.POST, projectBody("有料作品", "PAID"), HttpStatus.CREATED);
        UUID projectId = UUID.fromString(created.get("id").asText());

        assertThat(created.get("checklistTotal").asLong()).isEqualTo(36);
        JsonNode checklist = get("/api/v1/projects/" + projectId + "/checklist", HttpStatus.OK);
        boolean hasPaidItem = false;
        for (JsonNode item : checklist) {
            if (item.get("name").asText().contains("規約同意")) {
                hasPaidItem = true;
                break;
            }
        }
        assertThat(hasPaidItem).isTrue();
    }

    /**
     * 入力エラー時に、共通エラー形式のコードと項目別詳細が返ることを検証します。
     */
    @Test
    @DisplayName("入力エラー: 共通エラー形式で返す")
    void validationErrors_useCommonErrorShape() {
        JsonNode blankTitle = send("/api/v1/projects", HttpMethod.POST,
                mapOf("title", "  ", "releaseType", "FREE"), HttpStatus.BAD_REQUEST);
        assertThat(blankTitle.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");
        assertThat(blankTitle.get("error").get("details").get(0).get("field").asText()).isEqualTo("title");

        JsonNode invalidEnum = get("/api/v1/projects?status=UNKNOWN", HttpStatus.BAD_REQUEST);
        assertThat(invalidEnum.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");
    }

    /**
     * 存在しない作品やチェックリストへアクセスしたとき、404と共通エラー形式が返ることを検証します。
     */
    @Test
    @DisplayName("存在しないリソース: 404と共通エラー形式で返す")
    void missingResources_returnNotFound() {
        UUID missing = UUID.randomUUID();

        JsonNode project = get("/api/v1/projects/" + missing, HttpStatus.NOT_FOUND);
        assertThat(project.get("error").get("code").asText()).isEqualTo("NOT_FOUND");

        JsonNode checklist = get("/api/v1/projects/" + missing + "/checklist", HttpStatus.NOT_FOUND);
        assertThat(checklist.get("error").get("code").asText()).isEqualTo("NOT_FOUND");
    }

    /**
     * GETリクエストを送信し、期待するステータスのJSONを返します。
     *
     * @param path リクエストパス
     * @param expected 期待するHTTPステータス
     * @return レスポンスJSON
     */
    private JsonNode get(String path, HttpStatus expected) {
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
    private JsonNode send(String path, HttpMethod method, Object body, HttpStatus expected) {
        ResponseEntity<JsonNode> response = rest.exchange(path, method, entity(body), JsonNode.class);
        assertThat(response.getStatusCode()).isEqualTo(expected);
        return response.getBody();
    }

    /**
     * JSONリクエスト用のHTTPエンティティを生成します。
     *
     * @param body リクエスト本文
     * @return Content-TypeをJSONに設定したHTTPエンティティ
     */
    private HttpEntity<Object> entity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }

    /**
     * 作品作成APIへ送るリクエスト本文を生成します。
     *
     * @param title 作品タイトル
     * @param releaseType 公開形態
     * @return リクエスト本文
     */
    private Map<String, Object> projectBody(String title, String releaseType) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("releaseType", releaseType);
        return body;
    }

    /**
     * キーと値の組からリクエスト本文を生成します。
     *
     * @param pairs キーと値の組
     * @return リクエスト本文
     */
    private Map<String, Object> mapOf(Object... pairs) {
        Map<String, Object> body = new LinkedHashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            body.put((String) pairs[i], pairs[i + 1]);
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
    private boolean containsId(JsonNode array, UUID id) {
        for (JsonNode node : array) {
            if (node.get("id").asText().equals(id.toString())) {
                return true;
            }
        }
        return false;
    }
}
