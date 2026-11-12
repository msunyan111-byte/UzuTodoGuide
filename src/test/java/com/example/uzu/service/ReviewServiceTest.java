package com.example.uzu.service;

import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.domain.ReviewStatus;
import com.example.uzu.domain.ReviewType;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.example.uzu.web.dto.ReviewRecordResponse;
import com.example.uzu.web.dto.ReviewRecordUpdateRequest;
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
import static org.mockito.BDDMockito.given;

/**
 * {@link ReviewService} の審査記録更新を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService の単体テスト")
class ReviewServiceTest {

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /** 審査記録リポジトリのモック。 */
    @Mock
    private ReviewRecordRepository reviewRecordRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private ReviewService reviewService;

    /**
     * 更新リクエストの全項目が審査記録へ反映されることを検証します。
     */
    @Test
    @DisplayName("審査記録の更新: 指定された全項目を反映する")
    void update_appliesAllProvidedFields() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewRecordRepository.saveAndFlush(record)).willReturn(record);

        ReviewRecordUpdateRequest request = new ReviewRecordUpdateRequest();
        request.setReviewType(ReviewType.PAID);
        request.setSubmittedAt(LocalDate.of(2026, 11, 12));
        request.setStatus(ReviewStatus.PASSED);
        request.setResult("合格");
        request.setMemo("公開可");

        ReviewRecordResponse response = reviewService.update(projectId, reviewId, request);

        assertThat(response.getReviewType()).isEqualTo(ReviewType.PAID);
        assertThat(response.getSubmittedAt()).isEqualTo(LocalDate.of(2026, 11, 12));
        assertThat(response.getStatus()).isEqualTo(ReviewStatus.PASSED);
        assertThat(response.getResult()).isEqualTo("合格");
        assertThat(response.getMemo()).isEqualTo("公開可");
    }

    /**
     * 更新リクエストが空の場合、既存の審査記録を変更しないことを検証します。
     */
    @Test
    @DisplayName("審査記録の更新: 未指定項目は変更しない")
    void update_ignoresUnspecifiedFields() {
        UUID projectId = UUID.randomUUID();
        UUID reviewId = UUID.randomUUID();
        ReviewRecord record = new ReviewRecord(reviewId, projectId, ReviewType.FREE);
        record.setSubmittedAt(LocalDate.of(2026, 11, 1));
        record.setStatus(ReviewStatus.SUBMITTED);
        record.setResult("結果");
        record.setMemo("メモ");
        given(reviewRecordRepository.findById(reviewId)).willReturn(Optional.of(record));
        given(reviewRecordRepository.saveAndFlush(record)).willReturn(record);

        ReviewRecordResponse response = reviewService.update(
                projectId, reviewId, new ReviewRecordUpdateRequest());

        assertThat(response.getReviewType()).isEqualTo(ReviewType.FREE);
        assertThat(response.getSubmittedAt()).isEqualTo(LocalDate.of(2026, 11, 1));
        assertThat(response.getStatus()).isEqualTo(ReviewStatus.SUBMITTED);
        assertThat(response.getResult()).isEqualTo("結果");
        assertThat(response.getMemo()).isEqualTo("メモ");
    }
}
