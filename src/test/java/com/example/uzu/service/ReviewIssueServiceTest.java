package com.example.uzu.service;

import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewIssueStatus;
import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.domain.ReviewType;
import com.example.uzu.repository.ReviewIssueRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.example.uzu.web.dto.ReviewIssueCreateRequest;
import com.example.uzu.web.dto.ReviewIssueResponse;
import com.example.uzu.web.dto.ReviewIssueUpdateRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

/**
 * {@link ReviewIssueService} の審査指摘事項更新を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewIssueService の単体テスト")
class ReviewIssueServiceTest {

    /** 審査記録リポジトリのモック。 */
    @Mock
    private ReviewRecordRepository reviewRecordRepository;

    /** 審査指摘事項リポジトリのモック。 */
    @Mock
    private ReviewIssueRepository reviewIssueRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private ReviewIssueService reviewIssueService;

    /**
     * 指摘事項を追加するとき、状態が未指定ならOPENになることを検証します。
     */
    @Test
    @DisplayName("審査指摘事項の追加: 状態未指定時はOPENにする")
    void addIssue_usesOpenStatusWhenNotSpecified() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewIssueRepository.saveAndFlush(any(ReviewIssue.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ReviewIssueCreateRequest request = new ReviewIssueCreateRequest();
        request.setContent("修正してください");

        ReviewIssueResponse response = reviewIssueService.addIssue(projectId, reviewId, request);

        assertThat(response.getStatus()).isEqualTo(ReviewIssueStatus.OPEN);
        assertThat(response.getResponseMemo()).isNull();
    }

    /**
     * 指摘事項の更新リクエストが空の場合、既存の指摘事項を変更しないことを検証します。
     */
    @Test
    @DisplayName("審査指摘事項の更新: 未指定項目は変更しない")
    void updateIssue_ignoresUnspecifiedFields() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        ReviewIssue issue = new ReviewIssue(issueId, reviewId, "既存指摘");
        issue.setStatus(ReviewIssueStatus.IN_PROGRESS);
        issue.setResponseMemo("対応中");
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewIssueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(reviewIssueRepository.saveAndFlush(issue)).willReturn(issue);

        ReviewIssueResponse response = reviewIssueService.updateIssue(
                projectId, reviewId, issueId, new ReviewIssueUpdateRequest());

        assertThat(response.getContent()).isEqualTo("既存指摘");
        assertThat(response.getStatus()).isEqualTo(ReviewIssueStatus.IN_PROGRESS);
        assertThat(response.getResponseMemo()).isEqualTo("対応中");
    }

    /**
     * 指摘事項を追加するとき、状態と対応メモが反映されることを検証します。
     */
    @Test
    @DisplayName("審査指摘事項の追加: 状態と対応メモを反映する")
    void addIssue_appliesStatusAndResponseMemo() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewIssueRepository.saveAndFlush(any(ReviewIssue.class)))
                .willAnswer(invocation -> invocation.getArgument(0));

        ReviewIssueCreateRequest request = new ReviewIssueCreateRequest();
        request.setContent(" 誤字を修正してください ");
        request.setStatus(ReviewIssueStatus.IN_PROGRESS);
        request.setResponseMemo("修正中");

        ReviewIssueResponse response = reviewIssueService.addIssue(projectId, reviewId, request);

        assertThat(response.getContent()).isEqualTo("誤字を修正してください");
        assertThat(response.getStatus()).isEqualTo(ReviewIssueStatus.IN_PROGRESS);
        assertThat(response.getResponseMemo()).isEqualTo("修正中");
    }

    /**
     * 指摘事項を更新するとき、内容・状態・対応メモが反映されることを検証します。
     */
    @Test
    @DisplayName("審査指摘事項の更新: 指定された全項目を反映する")
    void updateIssue_appliesAllProvidedFields() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        UUID issueId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        ReviewIssue issue = new ReviewIssue(issueId, reviewId, "旧指摘");
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewIssueRepository.findById(issueId)).willReturn(Optional.of(issue));
        given(reviewIssueRepository.saveAndFlush(issue)).willReturn(issue);

        ReviewIssueUpdateRequest request = new ReviewIssueUpdateRequest();
        request.setContent(" 新しい指摘 ");
        request.setStatus(ReviewIssueStatus.RESOLVED);
        request.setResponseMemo("対応済み");

        ReviewIssueResponse response = reviewIssueService.updateIssue(projectId, reviewId, issueId, request);

        assertThat(response.getContent()).isEqualTo("新しい指摘");
        assertThat(response.getStatus()).isEqualTo(ReviewIssueStatus.RESOLVED);
        assertThat(response.getResponseMemo()).isEqualTo("対応済み");
    }
}
