package com.example.uzu.service;

import com.example.uzu.domain.ProjectSectionState;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ProjectSectionStateRepository;
import com.example.uzu.web.dto.ProjectSectionStateResponse;
import com.example.uzu.web.dto.ProjectSectionStateUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * {@link ProjectSectionService} の取得・保存・検証を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectSectionService の単体テスト")
class ProjectSectionServiceTest {

    /** セクション状態リポジトリのモック。 */
    @Mock
    private ProjectSectionStateRepository projectSectionStateRepository;

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private ProjectSectionService projectSectionService;

    /**
     * 存在しない作品のセクション状態を取得したとき、NotFoundExceptionが送出されることを検証します。
     */
    @Test
    @DisplayName("セクション状態取得: 存在しない作品ではNotFoundを返す")
    void find_throwsWhenProjectMissing() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(false);

        assertThatThrownBy(() -> projectSectionService.find(projectId, "SUMMARY"))
                .isInstanceOf(NotFoundException.class);
    }

    /**
     * 保存済みのセクション状態を取得できることを検証します。
     */
    @Test
    @DisplayName("セクション状態取得: 保存済みのメモと確認日時を返す")
    void find_returnsStoredState() {
        UUID projectId = UUID.randomUUID();
        ProjectSectionState state = new ProjectSectionState(UUID.randomUUID(), projectId, "SUMMARY");
        state.setMemo("確認済み");
        state.setVerifiedAt(LocalDateTime.of(2026, 11, 12, 10, 0));
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectSectionStateRepository.findByProjectIdAndSectionKey(projectId, "SUMMARY"))
                .willReturn(Optional.of(state));

        ProjectSectionStateResponse response = projectSectionService.find(projectId, "SUMMARY");

        assertThat(response.getSectionKey()).isEqualTo("SUMMARY");
        assertThat(response.getMemo()).isEqualTo("確認済み");
        assertThat(response.getVerifiedAt()).isEqualTo(
                OffsetDateTime.parse("2026-11-12T10:00:00+09:00"));
    }

    /**
     * 未保存のセクションへメモと確認日時を保存できることを検証します。
     */
    @Test
    @DisplayName("セクション状態保存: 未保存なら新規作成する")
    void save_createsNewState() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectSectionStateRepository.findByProjectIdAndSectionKey(projectId, "CHARACTERS"))
                .willReturn(Optional.empty());
        given(projectSectionStateRepository.saveAndFlush(any(ProjectSectionState.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ProjectSectionStateUpdateRequest request = new ProjectSectionStateUpdateRequest();
        request.setMemo("関係性を確認");
        request.setVerifiedAt(OffsetDateTime.parse("2026-11-12T10:00:00Z"));

        ProjectSectionStateResponse response = projectSectionService.save(projectId, "CHARACTERS", request);

        assertThat(response.getSectionKey()).isEqualTo("CHARACTERS");
        assertThat(response.getMemo()).isEqualTo("関係性を確認");
        assertThat(response.getVerifiedAt()).isEqualTo(
                OffsetDateTime.parse("2026-11-12T19:00:00+09:00"));
        verify(projectSectionStateRepository).saveAndFlush(any(ProjectSectionState.class));
    }

    /**
     * 既存のセクション状態を更新できることを検証します。
     */
    @Test
    @DisplayName("セクション状態保存: 既存の状態を更新する")
    void save_updatesExistingState() {
        UUID projectId = UUID.randomUUID();
        ProjectSectionState state = new ProjectSectionState(UUID.randomUUID(), projectId, "SUMMARY");
        state.setMemo("古いメモ");
        state.setVerifiedAt(LocalDateTime.of(2026, 11, 1, 9, 0));
        given(projectRepository.existsById(projectId)).willReturn(true);
        given(projectSectionStateRepository.findByProjectIdAndSectionKey(projectId, "SUMMARY"))
                .willReturn(Optional.of(state));
        given(projectSectionStateRepository.saveAndFlush(state)).willReturn(state);

        ProjectSectionStateUpdateRequest request = new ProjectSectionStateUpdateRequest();
        request.setMemo("新しいメモ");

        ProjectSectionStateResponse response = projectSectionService.save(projectId, "SUMMARY", request);

        assertThat(response.getMemo()).isEqualTo("新しいメモ");
        assertThat(response.getVerifiedAt()).isNull();
    }

    /**
     * セクションキーの形式が不正な場合、入力エラーが送出されることを検証します。
     */
    @Test
    @DisplayName("セクション状態保存: 不正なセクションキーを拒否する")
    void save_rejectsInvalidSectionKey() {
        UUID projectId = UUID.randomUUID();
        given(projectRepository.existsById(projectId)).willReturn(true);

        assertThatThrownBy(() -> projectSectionService.save(
                projectId, "summary", new ProjectSectionStateUpdateRequest()))
                .isInstanceOf(ApiValidationException.class);
    }
}
