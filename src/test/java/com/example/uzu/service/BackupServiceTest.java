package com.example.uzu.service;

import com.example.uzu.domain.Project;
import com.example.uzu.domain.ReleaseType;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.GuideArticleRepository;
import com.example.uzu.repository.GuideFaqRepository;
import com.example.uzu.repository.PlaytestRecordRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ReviewIssueRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link BackupService} のエクスポート・復元を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("BackupService の単体テスト")
class BackupServiceTest {

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** チェックリストリポジトリのモック。 */
    @Mock
    private ChecklistItemRepository checklistItemRepository;

    /** テストプレイ記録リポジトリのモック。 */
    @Mock
    private PlaytestRecordRepository playtestRecordRepository;

    /** 審査記録リポジトリのモック。 */
    @Mock
    private ReviewRecordRepository reviewRecordRepository;

    /** 審査指摘事項リポジトリのモック。 */
    @Mock
    private ReviewIssueRepository reviewIssueRepository;

    /** ガイド記事リポジトリのモック。 */
    @Mock
    private GuideArticleRepository guideArticleRepository;

    /** ガイドQ&Aリポジトリのモック。 */
    @Mock
    private GuideFaqRepository guideFaqRepository;

    /** JSON変換用オブジェクトマッパーのモック。 */
    @Mock
    private ObjectMapper objectMapper;

    /** テスト対象のサービス。 */
    @InjectMocks
    private BackupService backupService;

    /**
     * 復元データがnullまたは空の場合、現在の全データを返すことを検証します。
     */
    @Test
    @DisplayName("復元: nullまたは空のデータでは現状をエクスポートする")
    void restore_returnsExportForNullOrEmptyPayload() {
        Project project = new Project(UUID.randomUUID(), "作品", ReleaseType.FREE);
        given(projectRepository.findAll()).willReturn(List.of(project));

        Map<String, Object> nullResult = backupService.restore(null);
        Map<String, Object> emptyResult = backupService.restore(Map.of());

        assertThat(nullResult).containsEntry("projects", List.of(project));
        assertThat(emptyResult).containsEntry("projects", List.of(project));
        assertThat(emptyResult).containsKeys(
                "projects", "checklistItems", "playtestRecords", "reviewRecords",
                "reviewIssues", "guideArticles", "guideFaqs");
    }

    /**
     * 一覧でない値やMapでない要素を無視し、保存処理を呼ばないことを検証します。
     */
    @Test
    @DisplayName("復元: 一覧でない値とMapでない要素を無視する")
    void restore_ignoresInvalidListEntries() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("projects", List.of("not-map"));
        payload.put("checklistItems", "not-list");

        backupService.restore(payload);

        verify(projectRepository, never()).saveAll(anyList());
        verify(checklistItemRepository, never()).saveAll(anyList());
        verify(objectMapper, never()).convertValue(any(), eq(Project.class));
    }

    /**
     * Map形式の要素をエンティティへ変換し、変換できた場合だけ保存することを検証します。
     */
    @Test
    @DisplayName("復元: Map形式の要素を変換して保存する")
    void restore_convertsMapItemsAndSavesEntities() {
        Project project = new Project(UUID.randomUUID(), "作品", ReleaseType.FREE);
        Map<String, Object> item = Map.of("title", "作品");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("projects", List.of(item));
        given(objectMapper.convertValue(item, Project.class)).willReturn(project);
        given(projectRepository.saveAll(anyList())).willReturn(List.of(project));

        backupService.restore(payload);

        verify(projectRepository).saveAll(List.of(project));
    }
}
