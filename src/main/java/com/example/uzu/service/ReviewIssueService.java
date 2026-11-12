package com.example.uzu.service;

import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.repository.ReviewIssueRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.example.uzu.web.dto.ReviewIssueCreateRequest;
import com.example.uzu.web.dto.ReviewIssueResponse;
import com.example.uzu.web.dto.ReviewIssueUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 作品の審査指摘事項を管理するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class ReviewIssueService {

    /**
     * 審査記録リポジトリ
     */
    private final ReviewRecordRepository reviewRecordRepository;

    /**
     * 審査指摘事項リポジトリ
     */
    private final ReviewIssueRepository reviewIssueRepository;

    /**
     * 審査指摘事項サービスを生成します。
     *
     * @param reviewRecordRepository 審査記録リポジトリ
     * @param reviewIssueRepository 審査指摘事項リポジトリ
     */
    public ReviewIssueService(final ReviewRecordRepository reviewRecordRepository,
                              final ReviewIssueRepository reviewIssueRepository) {
        this.reviewRecordRepository = reviewRecordRepository;
        this.reviewIssueRepository = reviewIssueRepository;
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
