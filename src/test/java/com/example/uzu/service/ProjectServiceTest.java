package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.domain.Project;
import com.example.uzu.domain.ReleaseType;
import com.example.uzu.domain.Stage;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.ProjectCreateRequest;
import com.example.uzu.web.dto.ProjectResponse;
import com.example.uzu.web.dto.ProjectUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link ProjectService} の作品作成・取得・更新・アーカイブを検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService の単体テスト")
class ProjectServiceTest {

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** チェック項目リポジトリのモック。 */
    @Mock
    private ChecklistItemRepository checklistItemRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private ProjectService projectService;

    /**
     * 無料作品を作成したとき、標準チェックリスト39件と初期表示順が設定されることを検証します。
     */
    @Test
    @DisplayName("無料作品の作成: 標準チェックリスト39件と初期表示順を設定する")
    void create_freeProject_createsStandardItems() {
        given(projectRepository.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<List<ChecklistItem>> captor = itemListCaptor();
        given(checklistItemRepository.saveAll(captor.capture())).willAnswer(invocation -> invocation.getArgument(0));
        given(checklistItemRepository.countByProjectId(any())).willReturn(39L);
        given(checklistItemRepository.countByProjectIdAndStatus(any(), eq(ChecklistStatus.DONE))).willReturn(0L);

        ProjectResponse response = projectService.create(request("夜明けの証言", ReleaseType.FREE));

        assertThat(response.getStage()).isEqualTo(Stage.PLANNING);
        assertThat(response.isArchived()).isFalse();
        assertThat(response.getChecklistTotal()).isEqualTo(39);
        List<ChecklistItem> items = captor.getValue();
        assertThat(items).hasSize(39);
        assertThat(items).extracting(ChecklistItem::getName)
                .contains("【作品登録】作品の基本情報を登録する", "【リリース管理】公開状態を最終確認する");
        assertThat(items).extracting(ChecklistItem::getDisplayOrder)
                .containsExactlyElementsOf(
                        IntStream.rangeClosed(1, 39).boxed().collect(Collectors.toList()));
    }

    /**
     * 有料作品を作成したとき、有料公開用のチェック項目を含む36件が作成されることを検証します。
     */
    @Test
    @DisplayName("有料作品の作成: 有料公開用チェックリスト41件を作成する")
    void create_paidProject_addsPaidSpecificItem() {
        given(projectRepository.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));
        ArgumentCaptor<List<ChecklistItem>> captor = itemListCaptor();
        given(checklistItemRepository.saveAll(captor.capture())).willAnswer(invocation -> invocation.getArgument(0));
        given(checklistItemRepository.countByProjectId(any())).willReturn(41L);
        given(checklistItemRepository.countByProjectIdAndStatus(any(), eq(ChecklistStatus.DONE))).willReturn(0L);

        ProjectResponse response = projectService.create(request("有料作品", ReleaseType.PAID));

        assertThat(response.getChecklistTotal()).isEqualTo(41);
        assertThat(captor.getValue()).extracting(ChecklistItem::getName)
                .contains("【規約同意】売上受取人全員の同意を確認する");
    }

    /**
     * 作成時に制作段階を指定したとき、その段階がそのまま採用されることを検証します。
     */
    @Test
    @DisplayName("作品の作成: 指定された制作段階をそのまま採用する")
    void create_acceptsExplicitStage() {
        given(projectRepository.save(any(Project.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(checklistItemRepository.saveAll(anyCollection())).willAnswer(invocation -> invocation.getArgument(0));
        given(checklistItemRepository.countByProjectId(any())).willReturn(39L);
        given(checklistItemRepository.countByProjectIdAndStatus(any(), eq(ChecklistStatus.DONE))).willReturn(0L);

        ProjectCreateRequest request = request("制作中作品", ReleaseType.FREE);
        request.setStage(Stage.IN_PROGRESS);

        assertThat(projectService.create(request).getStage()).isEqualTo(Stage.IN_PROGRESS);
    }

    /**
     * 存在しない作品IDを指定したとき、NotFoundExceptionが送出されることを検証します。
     */
    @Test
    @DisplayName("作品の取得: 存在しないIDではNotFoundを返す")
    void findById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        given(projectRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("作品が見つかりません。");
    }

    /**
     * 更新リクエストで指定した項目だけが反映され、未指定項目は変更されないことを検証します。
     */
    @Test
    @DisplayName("作品の更新: 指定された項目だけを反映する")
    void update_appliesOnlyProvidedFields() {
        UUID id = UUID.randomUUID();
        Project project = project(id, "old");
        given(projectRepository.findById(id)).willReturn(Optional.of(project));
        given(projectRepository.saveAndFlush(project)).willReturn(project);
        given(checklistItemRepository.countByProjectId(id)).willReturn(13L);
        given(checklistItemRepository.countByProjectIdAndStatus(id, ChecklistStatus.DONE)).willReturn(0L);

        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setTitle("new title");
        request.setStage(Stage.PLAYTEST);

        ProjectResponse response = projectService.update(id, request);

        assertThat(response.getTitle()).isEqualTo("new title");
        assertThat(response.getStage()).isEqualTo(Stage.PLAYTEST);
        assertThat(response.getFurigana()).isNull();
        verify(projectRepository).saveAndFlush(project);
    }

    /**
     * 空白だけのタイトルで更新したとき、入力エラーが送出されることを検証します。
     */
    @Test
    @DisplayName("作品の更新: 空白だけのタイトルを拒否する")
    void update_rejectsBlankTitle() {
        UUID id = UUID.randomUUID();
        given(projectRepository.findById(id)).willReturn(Optional.of(project(id, "old")));

        ProjectUpdateRequest request = new ProjectUpdateRequest();
        request.setTitle("   ");

        assertThatThrownBy(() -> projectService.update(id, request))
                .isInstanceOf(ApiValidationException.class);
    }

    /**
     * 作品をアーカイブしたとき、レスポンスとエンティティの両方でフラグが立つことを検証します。
     */
    @Test
    @DisplayName("作品のアーカイブ: アーカイブ済みフラグを立てる")
    void archive_setsArchivedFlag() {
        UUID id = UUID.randomUUID();
        Project project = project(id, "title");
        given(projectRepository.findById(id)).willReturn(Optional.of(project));
        given(projectRepository.saveAndFlush(project)).willReturn(project);
        given(checklistItemRepository.countByProjectId(id)).willReturn(13L);
        given(checklistItemRepository.countByProjectIdAndStatus(id, ChecklistStatus.DONE)).willReturn(0L);

        ProjectResponse response = projectService.archive(id);

        assertThat(response.isArchived()).isTrue();
        assertThat(project.isArchived()).isTrue();
    }

    /**
     * チェック項目のリストを取得する引数キャプチャを生成します。
     *
     * @return チェック項目リスト用の引数キャプチャ
     */
    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<ChecklistItem>> itemListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    /**
     * テスト用の作品作成リクエストを生成します。
     *
     * @param title 作品タイトル
     * @param releaseType 公開形態
     * @return 作品作成リクエスト
     */
    private ProjectCreateRequest request(String title, ReleaseType releaseType) {
        ProjectCreateRequest request = new ProjectCreateRequest();
        request.setTitle(title);
        request.setReleaseType(releaseType);
        return request;
    }

    /**
     * テスト用の作品エンティティを生成します。
     *
     * @param id 作品ID
     * @param title 作品タイトル
     * @return 無料公開の作品エンティティ
     */
    private Project project(UUID id, String title) {
        return new Project(id, title, ReleaseType.FREE);
    }
}
