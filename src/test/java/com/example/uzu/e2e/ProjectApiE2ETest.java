package com.example.uzu.e2e;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PlaywrightのAPIリクエストで作品APIの作成からアーカイブまでを検証するE2Eテストです。
 */
@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@DisplayName("作品 API の Playwright E2E テスト")
class ProjectApiE2ETest {

    /** JSONの読み書きに使うObjectMapper。 */
    private static final ObjectMapper MAPPER = new ObjectMapper();

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

    /** テスト対象のサーバーポート。 */
    @LocalServerPort
    private int port;

    /** Playwrightの実行基盤。 */
    private static Playwright playwright;

    /** APIリクエスト用のコンテキスト。 */
    private static APIRequestContext api;

    /**
     * PlaywrightとAPIリクエストコンテキストを初期化します。
     */
    @BeforeAll
    static void startPlaywright() {
        playwright = Playwright.create();
        api = playwright.request().newContext(new APIRequest.NewContextOptions());
    }

    /**
     * APIリクエストコンテキストとPlaywrightを解放します。
     */
    @AfterAll
    static void stopPlaywright() {
        if (api != null) {
            api.dispose();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    /**
     * 作品作成・チェック更新・アーカイブ・入力エラー確認をPlaywrightのAPIリクエストで検証します。
     *
     * @throws Exception JSONの読み書きに失敗した場合
     */
    @Test
    @DisplayName("作品ライフサイクル: 作成からアーカイブまでをAPIリクエストで検証する")
    void projectLifecycleThroughPlaywright() throws Exception {
        APIResponse createdResponse = api.post(baseUrl(),
                jsonRequest(mapOf("title", "E2E作品", "releaseType", "PAID")));
        assertThat(createdResponse.status()).isEqualTo(201);
        JsonNode created = MAPPER.readTree(createdResponse.text());
        String projectId = created.get("id").asText();
        assertThat(created.get("stage").asText()).isEqualTo("PLANNING");
        assertThat(created.get("checklistTotal").asLong()).isEqualTo(44);

        APIResponse checklistResponse = api.get(baseUrl() + "/" + projectId + "/checklist");
        assertThat(checklistResponse.status()).isEqualTo(200);
        JsonNode checklist = MAPPER.readTree(checklistResponse.text());
        assertThat(checklist).hasSize(44);
        String itemId = checklist.get(0).get("id").asText();

        APIResponse patchedItemResponse = api.patch(
                baseUrl() + "/" + projectId + "/checklist/" + itemId,
                jsonRequest(mapOf("status", "DONE")));
        assertThat(patchedItemResponse.status()).isEqualTo(200);
        assertThat(MAPPER.readTree(patchedItemResponse.text()).get("status").asText()).isEqualTo("DONE");

        APIResponse projectResponse = api.get(baseUrl() + "/" + projectId);
        assertThat(projectResponse.status()).isEqualTo(200);
        assertThat(MAPPER.readTree(projectResponse.text()).get("checklistCompleted").asLong()).isEqualTo(1);

        APIResponse archivedResponse = api.post(baseUrl() + "/" + projectId + "/archive");
        assertThat(archivedResponse.status()).isEqualTo(200);
        assertThat(MAPPER.readTree(archivedResponse.text()).get("archived").asBoolean()).isTrue();

        APIResponse archivedListResponse = api.get(baseUrl() + "?archived=true");
        assertThat(archivedListResponse.status()).isEqualTo(200);
        assertThat(archivedListResponse.text()).contains(projectId);

        APIResponse validationResponse = api.post(baseUrl(),
                jsonRequest(mapOf("title", " ", "releaseType", "FREE")));
        assertThat(validationResponse.status()).isEqualTo(400);
        JsonNode error = MAPPER.readTree(validationResponse.text());
        assertThat(error.get("error").get("code").asText()).isEqualTo("VALIDATION_ERROR");
        assertThat(error.get("error").get("details").get(0).get("field").asText()).isEqualTo("title");
    }

    /**
     * テスト対象の作品APIのベースURLを返します。
     *
     * @return 作品APIのベースURL
     */
    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/projects";
    }

    /**
     * JSON本文を送信するためのリクエストオプションを生成します。
     *
     * @param body リクエスト本文
     * @return JSON用のリクエストオプション
     * @throws Exception JSONへの変換に失敗した場合
     */
    private RequestOptions jsonRequest(Map<String, Object> body) throws Exception {
        return RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setData(MAPPER.writeValueAsString(body));
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
}
