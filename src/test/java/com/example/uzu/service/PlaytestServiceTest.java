package com.example.uzu.service;

import com.example.uzu.domain.PlaytestPlatform;
import com.example.uzu.domain.PlaytestRecord;
import com.example.uzu.repository.PlaytestRecordRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.PlaytestRecordResponse;
import com.example.uzu.web.dto.PlaytestRecordUpdateRequest;
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
import static org.mockito.BDDMockito.given;

/**
 * {@link PlaytestService} の更新処理を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PlaytestService の単体テスト")
class PlaytestServiceTest {

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** テストプレイ記録リポジトリのモック。 */
    @Mock
    private PlaytestRecordRepository playtestRecordRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private PlaytestService playtestService;

    /**
     * 更新リクエストの全項目がテストプレイ記録へ反映されることを検証します。
     */
    @Test
    @DisplayName("テストプレイ記録の更新: 指定された全項目を反映する")
    void update_appliesAllProvidedFields() {
        UUID projectId = UUID.randomUUID();
        UUID playtestId = UUID.randomUUID();
        PlaytestRecord record = new PlaytestRecord(
                playtestId, projectId, LocalDate.of(2026, 11, 1), PlaytestPlatform.UZU);
        given(playtestRecordRepository.findById(playtestId)).willReturn(Optional.of(record));
        given(playtestRecordRepository.saveAndFlush(record)).willReturn(record);

        PlaytestRecordUpdateRequest request = new PlaytestRecordUpdateRequest();
        request.setPlayedAt(LocalDate.of(2026, 11, 12));
        request.setPlatform(PlaytestPlatform.OTHER);
        request.setParticipantCount(6);
        request.setVersionNote("v2.1");
        request.setQuestionnaireCollected(true);
        request.setQuestionnaireUrl("https://example.com/form");
        request.setFindings("分岐が分かりにくい");
        request.setMemo("次回修正");

        PlaytestRecordResponse response = playtestService.update(projectId, playtestId, request);

        assertThat(response.getPlayedAt()).isEqualTo(LocalDate.of(2026, 11, 12));
        assertThat(response.getPlatform()).isEqualTo(PlaytestPlatform.OTHER);
        assertThat(response.getParticipantCount()).isEqualTo(6);
        assertThat(response.getVersionNote()).isEqualTo("v2.1");
        assertThat(response.isQuestionnaireCollected()).isTrue();
        assertThat(response.getQuestionnaireUrl()).isEqualTo("https://example.com/form");
        assertThat(response.getFindings()).isEqualTo("分岐が分かりにくい");
        assertThat(response.getMemo()).isEqualTo("次回修正");
    }


    /**
     * 更新リクエストが空の場合、既存のテストプレイ記録を変更しないことを検証します。
     */
    @Test
    @DisplayName("テストプレイ記録の更新: 未指定項目は変更しない")
    void update_ignoresUnspecifiedFields() {
        UUID projectId = UUID.randomUUID();
        UUID playtestId = UUID.randomUUID();
        PlaytestRecord record = new PlaytestRecord(
                playtestId, projectId, LocalDate.of(2026, 11, 1), PlaytestPlatform.UZU);
        record.setParticipantCount(4);
        record.setVersionNote("v1");
        record.setQuestionnaireCollected(true);
        record.setFindings("気づき");
        record.setMemo("メモ");
        given(playtestRecordRepository.findById(playtestId)).willReturn(Optional.of(record));
        given(playtestRecordRepository.saveAndFlush(record)).willReturn(record);

        PlaytestRecordResponse response = playtestService.update(
                projectId, playtestId, new PlaytestRecordUpdateRequest());

        assertThat(response.getPlayedAt()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(response.getPlatform()).isEqualTo(PlaytestPlatform.UZU);
        assertThat(response.getParticipantCount()).isEqualTo(4);
        assertThat(response.getVersionNote()).isEqualTo("v1");
        assertThat(response.isQuestionnaireCollected()).isTrue();
        assertThat(response.getFindings()).isEqualTo("気づき");
        assertThat(response.getMemo()).isEqualTo("メモ");
    }

    /**
     * 別の作品に属するテストプレイ記録を更新しようとしたとき、NotFoundを返すことを検証します。
     */
    @Test
    @DisplayName("テストプレイ記録の更新: 別作品の記録ではNotFoundを返す")
    void update_throwsWhenRecordBelongsToAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID playtestId = UUID.randomUUID();
        PlaytestRecord record = new PlaytestRecord(
                playtestId, UUID.randomUUID(), LocalDate.of(2026, 11, 1), PlaytestPlatform.UZU);
        given(playtestRecordRepository.findById(playtestId)).willReturn(Optional.of(record));

        assertThatThrownBy(() -> playtestService.update(projectId, playtestId, new PlaytestRecordUpdateRequest()))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("テストプレイ記録が見つかりません。");
    }
}
