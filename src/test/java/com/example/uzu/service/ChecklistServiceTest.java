package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.ChecklistItemCreateRequest;
import com.example.uzu.web.dto.ChecklistItemResponse;
import com.example.uzu.web.dto.ChecklistItemUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link ChecklistService} のチェックリスト取得・追加・更新・削除を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ChecklistService の単体テスト")
class ChecklistServiceTest {

    /** チェック項目リポジトリのモック。 */
    @Mock
    private ChecklistItemRepository checklistItemRepository;

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private ChecklistService checklistService;

    /**
     * 存在しない作品のチェックリストを取得したとき、NotFoundExceptionが送出されることを検証します。
     */
    @Test
    @DisplayName("チェックリスト取得: 存在しない作品ではNotFoundを返す")
    void findAll_throwsWhenProjectMissing() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(false);

        assertThatThrownBy(() -> checklistService.findAll(projectId))
                .isInstanceOf(NotFoundException.class);
    }

    /**
     * 表示順を指定せずに項目を追加したとき、現在の件数の次の値が使われることを検証します。
     */
    @Test
    @DisplayName("チェック項目の追加: 表示順未指定時は現在件数の次を使う")
    void add_usesNextDisplayOrderWhenNotSpecified() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(checklistItemRepository.countByProjectId(projectId)).willReturn(13L);
        given(checklistItemRepository.saveAndFlush(any(ChecklistItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
        request.setName("追加項目");
        request.setDueDate(LocalDate.of(2026, 12, 1));

        ChecklistItemResponse response = checklistService.add(projectId, request);

        assertThat(response.getName()).isEqualTo("追加項目");
        assertThat(response.getDisplayOrder()).isEqualTo(14);
        assertThat(response.getStatus()).isEqualTo(ChecklistStatus.NOT_STARTED);
        assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 1));
    }

    /**
     * 状態を指定して項目を追加したとき、指定した状態が使われることを検証します。
     */
    @Test
    @DisplayName("チェック項目の追加: 状態指定時は指定値を使う")
    void add_usesSpecifiedStatus() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(checklistItemRepository.countByProjectId(projectId)).willReturn(0L);
        given(checklistItemRepository.saveAndFlush(any(ChecklistItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
        request.setName("完了項目");
        request.setStatus(ChecklistStatus.DONE);

        ChecklistItemResponse response = checklistService.add(projectId, request);

        assertThat(response.getStatus()).isEqualTo(ChecklistStatus.DONE);
    }

    /**
     * 表示順を指定して項目を追加したとき、指定した表示順が使われることを検証します。
     */
    @Test
    @DisplayName("チェック項目の追加: 表示順指定時は指定値を使う")
    void add_usesSpecifiedDisplayOrder() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(checklistItemRepository.saveAndFlush(any(ChecklistItem.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ChecklistItemCreateRequest request = new ChecklistItemCreateRequest();
        request.setName("表示順指定項目");
        request.setDisplayOrder(3);

        ChecklistItemResponse response = checklistService.add(projectId, request);

        assertThat(response.getDisplayOrder()).isEqualTo(3);
    }

    /**
     * 更新リクエストの指定項目が反映され、名称の前後空白が除去されることを検証します。
     */
    @Test
    @DisplayName("チェック項目の更新: 指定項目を反映し名称の前後空白を除去する")
    void update_appliesProvidedFieldsAndTrimsName() {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ChecklistItem item = new ChecklistItem(itemId, projectId, "old", 1);
        given(checklistItemRepository.findByIdAndProjectId(itemId, projectId)).willReturn(Optional.of(item));
        given(checklistItemRepository.saveAndFlush(item)).willReturn(item);

        ChecklistItemUpdateRequest request = new ChecklistItemUpdateRequest();
        request.setName("  new name  ");
        request.setDescription("説明");
        request.setStatus(ChecklistStatus.DONE);
        request.setMemo("done memo");
        request.setDueDate(LocalDate.of(2026, 12, 31));
        request.setDisplayOrder(7);

        ChecklistItemResponse response = checklistService.update(projectId, itemId, request);

        assertThat(response.getName()).isEqualTo("new name");
        assertThat(response.getDescription()).isEqualTo("説明");
        assertThat(response.getStatus()).isEqualTo(ChecklistStatus.DONE);
        assertThat(response.getMemo()).isEqualTo("done memo");
        assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.getDisplayOrder()).isEqualTo(7);
    }


    /**
     * 更新リクエストが空の場合、既存のチェック項目を変更しないことを検証します。
     */
    @Test
    @DisplayName("チェック項目の更新: 未指定項目は変更しない")
    void update_ignoresUnspecifiedFields() {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ChecklistItem item = new ChecklistItem(itemId, projectId, "old", 1);
        item.setDescription("説明");
        item.setStatus(ChecklistStatus.DONE);
        item.setMemo("メモ");
        item.setDueDate(LocalDate.of(2026, 12, 31));
        item.setDisplayOrder(5);
        given(checklistItemRepository.findByIdAndProjectId(itemId, projectId)).willReturn(Optional.of(item));
        given(checklistItemRepository.saveAndFlush(item)).willReturn(item);

        ChecklistItemResponse response = checklistService.update(
                projectId, itemId, new ChecklistItemUpdateRequest());

        assertThat(response.getName()).isEqualTo("old");
        assertThat(response.getDescription()).isEqualTo("説明");
        assertThat(response.getStatus()).isEqualTo(ChecklistStatus.DONE);
        assertThat(response.getMemo()).isEqualTo("メモ");
        assertThat(response.getDueDate()).isEqualTo(LocalDate.of(2026, 12, 31));
        assertThat(response.getDisplayOrder()).isEqualTo(5);
    }

    /**
     * 空白だけの名称で更新したとき、入力エラーが送出されることを検証します。
     */
    @Test
    @DisplayName("チェック項目の更新: 空白だけの名称を拒否する")
    void update_rejectsBlankName() {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ChecklistItem item = new ChecklistItem(itemId, projectId, "old", 1);
        given(checklistItemRepository.findByIdAndProjectId(itemId, projectId)).willReturn(Optional.of(item));

        ChecklistItemUpdateRequest request = new ChecklistItemUpdateRequest();
        request.setName("  ");

        assertThatThrownBy(() -> checklistService.update(projectId, itemId, request))
                .isInstanceOf(ApiValidationException.class);
    }

    /**
     * チェック項目を削除したとき、対象項目がリポジトリから削除されることを検証します。
     */
    @Test
    @DisplayName("チェック項目の削除: 対象項目をリポジトリから削除する")
    void delete_removesItem() {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        ChecklistItem item = new ChecklistItem(itemId, projectId, "item", 1);
        given(checklistItemRepository.findByIdAndProjectId(itemId, projectId)).willReturn(Optional.of(item));

        checklistService.delete(projectId, itemId);

        verify(checklistItemRepository).delete(item);
    }

    /**
     * 存在しないチェック項目を削除したとき、NotFoundExceptionが送出されることを検証します。
     */
    @Test
    @DisplayName("チェック項目の削除: 存在しない項目ではNotFoundを返す")
    void delete_throwsWhenItemMissing() {
        UUID projectId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        given(checklistItemRepository.findByIdAndProjectId(itemId, projectId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> checklistService.delete(projectId, itemId))
                .isInstanceOf(NotFoundException.class);
    }
}
