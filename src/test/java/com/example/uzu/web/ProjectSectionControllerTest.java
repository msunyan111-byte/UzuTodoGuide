package com.example.uzu.web;

import com.example.uzu.service.ProjectSectionService;
import com.example.uzu.web.dto.ProjectSectionStateResponse;
import com.example.uzu.web.dto.ProjectSectionStateUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * {@link ProjectSectionController} のHTTP契約を検証するWeb MVCテストです。
 */
@WebMvcTest(controllers = ProjectSectionController.class)
@DisplayName("ProjectSectionController の Web MVC テスト")
class ProjectSectionControllerTest {

    /** HTTPリクエストを実行するMockMvc。 */
    @Autowired
    private MockMvc mockMvc;

    /** コントローラが利用するサービスのモック。 */
    @MockBean
    private ProjectSectionService projectSectionService;

    /**
     * セクション状態の取得が200とメモを返すことを検証します。
     */
    @Test
    @DisplayName("セクション状態取得: メモと確認日時をJSONで返す")
    void get_returnsState() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(projectSectionService.find(projectId, "SUMMARY")).willReturn(response(projectId, "SUMMARY", "確認済み"));

        mockMvc.perform(get("/api/v1/projects/{projectId}/sections/{sectionKey}", projectId, "SUMMARY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sectionKey").value("SUMMARY"))
                .andExpect(jsonPath("$.memo").value("確認済み"))
                .andExpect(jsonPath("$.verifiedAt").value("2026-11-12T10:00:00+09:00"));
    }

    /**
     * セクション状態の保存が200と保存内容を返すことを検証します。
     */
    @Test
    @DisplayName("セクション状態保存: 200と保存内容を返す")
    void save_returnsUpdatedState() throws Exception {
        UUID projectId = UUID.randomUUID();
        given(projectSectionService.save(eq(projectId), eq("SUMMARY"),
                any(ProjectSectionStateUpdateRequest.class)))
                .willReturn(response(projectId, "SUMMARY", "更新後"));

        mockMvc.perform(patch("/api/v1/projects/{projectId}/sections/{sectionKey}", projectId, "SUMMARY")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"memo\":\"更新後\",\"verifiedAt\":\"2026-11-12T10:00:00+09:00\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.memo").value("更新後"));
    }

    /**
     * テスト用のセクション状態レスポンスを生成します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @param memo メモ
     * @return セクション状態レスポンス
     */
    private ProjectSectionStateResponse response(UUID projectId, String sectionKey, String memo) {
        OffsetDateTime now = OffsetDateTime.parse("2026-11-12T10:00:00+09:00");
        return new ProjectSectionStateResponse(
                UUID.randomUUID(), projectId, sectionKey, memo, now, now, now);
    }
}
