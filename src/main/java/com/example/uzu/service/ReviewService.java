package com.example.uzu.service;

import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ReviewIssueRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.example.uzu.web.dto.ReviewIssueCreateRequest;
import com.example.uzu.web.dto.ReviewIssueResponse;
import com.example.uzu.web.dto.ReviewIssueUpdateRequest;
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
 * 作品の審査記録と審査指摘事項を管理するサービスです。
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
     * 審査指摘事項リポジトリ
     */
    private final ReviewIssueRepository reviewIssueRepository;

    /**
     * 審査サービスを生成します。
     *
     * @param projectRepository 作品リポジトリ
     * @param reviewRecordRepository 審査記録リポジトリ
     * @param reviewIssueRepository 審査指摘事項リポジトリ
     */
    public ReviewService(final ProjectRepository projectRepository,
                         final ReviewRecordRepository reviewRecordRepository,
                         final ReviewIssueRepository reviewIssueRepository) {
        this.projectRepository = projectRepository;
        this.reviewRecordRepository = reviewRecordRepository;
        this.reviewIssueRepository = reviewIssueRepository;
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
        return ReviewRecordResponse.from(reviewRecordRepository.saveAndFlush(reviewRecord));
    }

    /**
     * 指定された審査記録の指摘事項を取得します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @return 審査指摘事項
     * @throws NotFoundException 審査記録が見つからない場合
     */
    public List<ReviewIssueResponse> findIssues(final UUID projectId, final UUID reviewId) {
        requireReview(projectId, reviewId);
        return reviewIssueRepository.findByReviewIdOrderByCreatedAtAsc(reviewId).stream()
                .map(ReviewIssueResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 審査指摘事項を追加します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param request 審査指摘事項作成リクエスト
     * @return 追加した審査指摘事項
     * @throws NotFoundException 審査記録が見つからない場合
     * @throws ApiValidationException 指摘内容が空白のみの場合
     */
    @Transactional
    public ReviewIssueResponse addIssue(final UUID projectId, final UUID reviewId,
                                        final ReviewIssueCreateRequest request) {
        requireReview(projectId, reviewId);
        final String content = ServiceValidation.requireText(
                request.getContent(), "content", "指摘内容は必須です。");
        final ReviewIssue issue = new ReviewIssue(UUID.randomUUID(), reviewId, content);
        if (request.getStatus() != null) {
            issue.setStatus(request.getStatus());
        }
        issue.setResponseMemo(request.getResponseMemo());
        return ReviewIssueResponse.from(reviewIssueRepository.saveAndFlush(issue));
    }

    /**
     * 審査指摘事項を更新します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param issueId 審査指摘事項ID
     * @param request 審査指摘事項更新リクエスト
     * @return 更新後の審査指摘事項
     * @throws NotFoundException 審査指摘事項が見つからない場合
     * @throws ApiValidationException 指摘内容が空白のみの場合
     */
    @Transactional
    public ReviewIssueResponse updateIssue(final UUID projectId, final UUID reviewId, final UUID issueId,
                                           final ReviewIssueUpdateRequest request) {
        final ReviewIssue issue = requireIssue(projectId, reviewId, issueId);
        if (request.getContent() != null) {
            issue.setContent(ServiceValidation.requireText(
                    request.getContent(), "content", "指摘内容は必須です。"));
        }
        if (request.getStatus() != null) {
            issue.setStatus(request.getStatus());
        }
        if (request.getResponseMemo() != null) {
            issue.setResponseMemo(request.getResponseMemo());
        }
        return ReviewIssueResponse.from(reviewIssueRepository.saveAndFlush(issue));
    }

    /**
     * 審査指摘事項を削除します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param issueId 審査指摘事項ID
     * @throws NotFoundException 審査指摘事項が見つからない場合
     */
    @Transactional
    public void deleteIssue(final UUID projectId, final UUID reviewId, final UUID issueId) {
        final ReviewIssue issue = requireIssue(projectId, reviewId, issueId);
        reviewIssueRepository.delete(issue);
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

    /**
     * 指定された審査指摘事項を取得し、存在しない場合は例外をスローします。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param issueId 審査指摘事項ID
     * @return 審査指摘事項
     * @throws NotFoundException 審査指摘事項が見つからない場合
     */
    private ReviewIssue requireIssue(final UUID projectId, final UUID reviewId, final UUID issueId) {
        requireReview(projectId, reviewId);
        return reviewIssueRepository.findById(issueId)
                .filter(issue -> issue.getReviewId().equals(reviewId))
                .orElseThrow(() -> new NotFoundException("審査指摘事項が見つかりません。"));
    }
}
