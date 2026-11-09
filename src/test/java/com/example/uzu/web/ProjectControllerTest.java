package com.example.uzu.web;

import com.example.uzu.domain.ReleaseType;
import com.example.uzu.domain.ScoringRule;
import com.example.uzu.domain.Stage;
import com.example.uzu.service.ProjectService;
import com.example.uzu.web.dto.ProjectCreateRequest;
import com.example.uzu.web.dto.ProjectResponse;
import com.example.uzu.web.dto.ProjectUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.ErrorDetail;
import com.example.uzu.web.error.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ProjectController} とヘルスチェックのHTTP契約を検証するWeb MVCテストです。
 */
@WebMvcTest(controllers = {ProjectController.class, HealthController.class})
@DisplayName("ProjectController の Web MVC テスト")
class ProjectControllerTest {

    /** HTTPリクエストを実行するMockMvc。 */
    @Autowired
    private MockMvc mockMvc;

    /** JSONの変換に使うObjectMapper。 */
    @Autowired
    private ObjectMapper objectMapper;

    /** コントローラが利用するサービスのモック。 */
    @MockBean
    private ProjectService projectService;

    /**
     * ヘルスチェックが200とUPを返すことを検証します。
     */
    @Test
    @DisplayName("ヘルスチェック: UPを返す")
    void health_returnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    /**
     * 作品一覧が200と登録済み作品のJSONを返すことを検証します。
     */
    @Test
    @DisplayName("作品一覧: 登録済み作品をJSONで返す")
    void list_returnsProjects() throws Exception {
        given(projectService.findAll(null, null)).willReturn(List.of(response("夜明けの証言")));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("夜明けの証言"))
                .andExpect(jsonPath("$[0].stage").value("PLANNING"));
    }

    /**
     * 一覧APIのstatusとarchivedパラメータがサービスへ正しく渡されることを検証します。
     */
    @Test
    @DisplayName("作品一覧: statusとarchivedの絞り込み条件をサービスへ渡す")
    void list_passesFilters() throws Exception {
        given(projectService.findAll(Stage.PLAYTEST, false)).willReturn(List.of());

        mockMvc.perform(get("/api/v1/projects")
                        .param("status", "PLAYTEST")
                        .param("archived", "false"))
                .andExpect(status().isOk());
    }

    /**
     * 作品作成が201と作成された作品の内容を返すことを検証します。
     */
    @Test
    @DisplayName("作品作成: 201と作成内容を返す")
    void create_returnsCreatedProject() throws Exception {
        given(projectService.create(any(ProjectCreateRequest.class))).willReturn(response("新作"));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody("新作"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("新作"))
                .andExpect(jsonPath("$.scoringRule").value("TIE_ALLOWED"))
                .andExpect(jsonPath("$.checklistTotal").value(13))
                .andExpect(jsonPath("$.createdAt").value("2026-11-03T13:00:00+09:00"));
    }

    /**
     * 空白だけのタイトルで作成したとき、400と項目別の入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("作品作成: 空白タイトルを400で拒否する")
    void create_rejectsBlankTitle() throws Exception {
        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody("  "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.error.details[0].field").value("title"));

        verify(projectService, never()).create(any());
    }

    /**
     * 未知の公開形態で作成したとき、400の入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("作品作成: 未知の公開形態を400で拒否する")
    void create_rejectsUnknownReleaseType() throws Exception {
        Map<String, Object> body = createBody("新作");
        body.put("releaseType", "UNKNOWN");

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("VALIDATION_ERROR"));
    }

    /**
     * 存在しない作品IDを取得したとき、404と共通エラー形式を返すことを検証します。
     */
    @Test
    @DisplayName("作品取得: 存在しないIDでは404を返す")
    void get_returnsNotFoundForMissingProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(projectService.findById(projectId)).willThrow(new NotFoundException("作品が見つかりません。"));

        mockMvc.perform(get("/api/v1/projects/{id}", projectId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    /**
     * 作品更新が200と更新後の内容を返すことを検証します。
     */
    @Test
    @DisplayName("作品更新: 更新後の内容を返す")
    void update_returnsUpdatedProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(projectService.update(eq(projectId), any(ProjectUpdateRequest.class)))
                .willReturn(response("更新後"));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", "更新後");
        body.put("stage", "IN_PROGRESS");

        mockMvc.perform(patch("/api/v1/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("更新後"));
    }

    /**
     * 空白だけのタイトルで更新したとき、400とtitleの入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("作品更新: 空白タイトルを400で拒否する")
    void update_returnsValidationErrorForBlankTitle() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(projectService.update(eq(projectId), any(ProjectUpdateRequest.class)))
                .willThrow(new ApiValidationException(List.of(new ErrorDetail("title", "必須項目です。"))));

        mockMvc.perform(patch("/api/v1/projects/{id}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"  \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.details[0].field").value("title"));
    }

    /**
     * アーカイブAPIが200とarchived=trueを返すことを検証します。
     */
    @Test
    @DisplayName("作品アーカイブ: archived=trueを返す")
    void archive_returnsArchivedProject() throws Exception {
        UUID projectId = UUID.randomUUID();
        ProjectResponse archived = response("作品", true);
        given(projectService.archive(projectId)).willReturn(archived);

        mockMvc.perform(post("/api/v1/projects/{id}/archive", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }

    /**
     * 作品作成APIへ送る最小限のリクエスト本文を生成します。
     *
     * @param title 作品タイトル
     * @return リクエスト本文
     */
    private Map<String, Object> createBody(String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("releaseType", "FREE");
        return body;
    }

    /**
     * アーカイブされていない作品レスポンスを生成します。
     *
     * @param title 作品タイトル
     * @return 作品レスポンス
     */
    private ProjectResponse response(String title) {
        return response(title, false);
    }

    /**
     * テスト用の作品レスポンスを生成します。
     *
     * @param title 作品タイトル
     * @param archived アーカイブ済みかどうか
     * @return 作品レスポンス
     */
    private ProjectResponse response(String title, boolean archived) {
        OffsetDateTime now = OffsetDateTime.parse("2026-11-03T13:00:00+09:00");
        return new ProjectResponse(
                UUID.randomUUID(), title, null, null, null, null,
                ReleaseType.FREE, Stage.PLANNING, ScoringRule.TIE_ALLOWED, null, null, null, null,
                archived, 0, 13, now, now);
    }
}
