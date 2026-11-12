package com.example.uzu.service;

import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.example.uzu.web.dto.ReviewRecordCreateRequest;
import com.example.uzu.web.dto.ReviewRecordResponse;
import com.example.uzu.web.dto.ReviewRecordUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 作品の審査記録を管理するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class ReviewService {

    /**
     * 提出日の降順ソート
     */
    private static final Sort SUBMITTED_AT_DESC = Sort.by(Sort.Direction.DESC, "submittedAt");

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * 審査記録リポジトリ
     */
    private final ReviewRecordRepository reviewRecordRepository;

    /**
     * 審査サービスを生成します。
     *
     * @param projectRepository 作品リポジトリ
     * @param reviewRecordRepository 審査記録リポジトリ
     */
    public ReviewService(final ProjectRepository projectRepository,
                         final ReviewRecordRepository reviewRecordRepository) {
        this.projectRepository = projectRepository;
        this.reviewRecordRepository = reviewRecordRepository;
    }

    /**
     * 指定された作品の審査記録を取得します。
     *
     * @param projectId 作品ID
     * @return 審査記録
     * @throws NotFoundException 作品が見つからない場合
     */
    public List<ReviewRecordResponse> findAll(final UUID projectId) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        return reviewRecordRepository.findByProjectId(projectId, SUBMITTED_AT_DESC).stream()
                .map(ReviewRecordResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 審査記録を作成します。
     *
     * @param projectId 作品ID
     * @param request 審査記録作成リクエスト
     * @return 作成した審査記録
     * @throws NotFoundException 作品が見つからない場合
     * @throws ApiValidationException 審査種別が未指定の場合
     */
    @Transactional
    public ReviewRecordResponse create(final UUID projectId, final ReviewRecordCreateRequest request) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        final ReviewRecord reviewRecord = new ReviewRecord(
                UUID.randomUUID(),
                projectId,
                ServiceValidation.requireValue(request.getReviewType(), "reviewType", "審査種別は必須です。"));
        if (request.getSubmittedAt() != null) {
            reviewRecord.setSubmittedAt(request.getSubmittedAt());
        }
        if (request.getStatus() != null) {
            reviewRecord.setStatus(request.getStatus());
        }
        reviewRecord.setResult(request.getResult());
        reviewRecord.setMemo(request.getMemo());
        return ReviewRecordResponse.from(reviewRecordRepository.saveAndFlush(reviewRecord));
    }

    /**
     * 審査記録を更新します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param request 審査記録更新リクエスト
     * @return 更新後の審査記録
     * @throws NotFoundException 審査記録が見つからない場合
     */
    @Transactional
    public ReviewRecordResponse update(final UUID projectId, final UUID reviewId,
                                       final ReviewRecordUpdateRequest request) {
        final ReviewRecord reviewRecord = requireReview(projectId, reviewId);
        applyUpdate(reviewRecord, request);
        return ReviewRecordResponse.from(reviewRecordRepository.saveAndFlush(reviewRecord));
    }

    /**
     * 更新リクエストで指定された項目を審査記録へ反映します。
     *
     * @param reviewRecord 更新対象の審査記録
     * @param request 審査記録更新リクエスト
     */
    private void applyUpdate(final ReviewRecord reviewRecord, final ReviewRecordUpdateRequest request) {
        if (request.getReviewType() != null) {
            reviewRecord.setReviewType(request.getReviewType());
        }
        if (request.getSubmittedAt() != null) {
            reviewRecord.setSubmittedAt(request.getSubmittedAt());
        }
        if (request.getStatus() != null) {
            reviewRecord.setStatus(request.getStatus());
        }
        if (request.getResult() != null) {
            reviewRecord.setResult(request.getResult());
        }
        if (request.getMemo() != null) {
            reviewRecord.setMemo(request.getMemo());
        }
    }

    /**
     * 指定された審査記録を取得し、存在しない場合は例外をスローします。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @return 審査記録
     * @throws NotFoundException 審査記録が見つからない場合
     */
    private ReviewRecord requireReview(final UUID projectId, final UUID reviewId) {
        return reviewRecordRepository.findById(reviewId)
                .filter(review -> review.getProjectId().equals(projectId))
                .orElseThrow(() -> new NotFoundException("審査記録が見つかりません。"));
    }
}
