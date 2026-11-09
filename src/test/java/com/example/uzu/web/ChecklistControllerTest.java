package com.example.uzu.web;

import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.service.ChecklistService;
import com.example.uzu.web.dto.ChecklistItemCreateRequest;
import com.example.uzu.web.dto.ChecklistItemResponse;
import com.example.uzu.web.dto.ChecklistItemUpdateRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ChecklistController} のチェックリスト取得・追加・更新・削除のHTTP契約を検証するWeb MVCテストです。
 */
@WebMvcTest(controllers = ChecklistController.class)
@DisplayName("ChecklistController の Web MVC テスト")
class ChecklistControllerTest {

    /** HTTPリクエストを実行するMockMvc。 */
    @Autowired
    private MockMvc mockMvc;

    /** JSONの変換に使うObjectMapper。 */
    @Autowired
    private ObjectMapper objectMapper;

    /** コントローラが利用するサービスのモック。 */
    @MockBean
    private ChecklistService checklistService;

    /**
     * チェックリスト一覧が200と項目のJSONを返すことを検証します。
     */
    @Test
    @DisplayName("チェックリスト一覧: 項目をJSONで返す")
    void list_returnsItems() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(checklistService.findAll(projectId)).willReturn(List.of(item(projectId, "作品概要の確認", 1)));

        mockMvc.perform(get("/api/v1/projects/{projectId}/checklist", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("作品概要の確認"))
                .andExpect(jsonPath("$[0].displayOrder").value(1));
    }

    /**
     * 存在しない作品のチェックリストを取得したとき、404を返すことを検証します。
     */
    @Test
    @DisplayName("チェックリスト一覧: 存在しない作品では404を返す")
    void list_returnsNotFoundWhenProjectMissing() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(checklistService.findAll(projectId)).willThrow(new NotFoundException("作品が見つかりません。"));

        mockMvc.perform(get("/api/v1/projects/{projectId}/checklist", projectId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("NOT_FOUND"));
    }

    /**
     * チェック項目の作成が201と作成内容を返すことを検証します。
     */
    @Test
    @DisplayName("チェック項目作成: 201と作成内容を返す")
    void add_returnsCreatedItem() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(checklistService.add(eq(projectId), any(ChecklistItemCreateRequest.class)))
                .willReturn(item(projectId, "追加項目", 10));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "追加項目");

        mockMvc.perform(post("/api/v1/projects/{projectId}/checklist", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("追加項目"))
                .andExpect(jsonPath("$.displayOrder").value(10));
    }

    /**
     * 空白だけの名称で作成したとき、400とnameの入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("チェック項目作成: 空白名称を400で拒否する")
    void add_rejectsBlankName() throws Exception {
        UUID projectId = UUID.randomUUID();
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("name", "  ");

        mockMvc.perform(post("/api/v1/projects/{projectId}/checklist", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.details[0].field").value("name"));
    }

    /**
     * チェック項目の更新が200と更新後の内容を返すことを検証します。
     */
    @Test
    @DisplayName("チェック項目更新: 更新後の内容を返す")
    void update_returnsUpdatedItem() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ChecklistItemResponse done = item(itemId, projectId, "作品概要の確認", 1, ChecklistStatus.DONE);
        given(checklistService.update(eq(projectId), eq(itemId), any(ChecklistItemUpdateRequest.class)))
                .willReturn(done);

        mockMvc.perform(patch("/api/v1/projects/{projectId}/checklist/{itemId}", projectId, itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"DONE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"));
    }

    /**
     * チェック項目の削除が204を返すことを検証します。
     */
    @Test
    @DisplayName("チェック項目削除: 204を返す")
    void delete_returnsNoContent() throws Exception {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/projects/{projectId}/checklist/{itemId}", projectId, itemId))
                .andExpect(status().isNoContent());
    }

    /**
     * 未着手状態のチェック項目レスポンスを生成します。
     *
     * @param projectId 作品ID
     * @param name 項目名
     * @param order 表示順
     * @return チェック項目レスポンス
     */
    private ChecklistItemResponse item(UUID projectId, String name, int order) {
        return item(UUID.randomUUID(), projectId, name, order, ChecklistStatus.NOT_STARTED);
    }

    /**
     * テスト用のチェック項目レスポンスを生成します。
     *
     * @param itemId 項目ID
     * @param projectId 作品ID
     * @param name 項目名
     * @param order 表示順
     * @param status チェック状態
     * @return チェック項目レスポンス
     */
    private ChecklistItemResponse item(UUID itemId, UUID projectId, String name, int order, ChecklistStatus status) {
        OffsetDateTime now = OffsetDateTime.parse("2026-11-03T13:00:00+09:00");
        return new ChecklistItemResponse(
                itemId, projectId, name, null, status, null, null, order, now, now);
    }
}
