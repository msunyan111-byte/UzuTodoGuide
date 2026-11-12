package com.example.uzu.web;

import com.example.uzu.service.ReviewIssueService;
import com.example.uzu.service.ReviewService;
import com.example.uzu.web.dto.ReviewIssueCreateRequest;
import com.example.uzu.web.dto.ReviewIssueResponse;
import com.example.uzu.web.dto.ReviewIssueUpdateRequest;
import com.example.uzu.web.dto.ReviewRecordCreateRequest;
import com.example.uzu.web.dto.ReviewRecordResponse;
import com.example.uzu.web.dto.ReviewRecordUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * 審査記録と審査指摘事項のAPIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}")
public class ReviewController {

    /**
     * 審査記録サービス
     */
    private final ReviewService reviewService;

    /**
     * 審査指摘事項サービス
     */
    private final ReviewIssueService reviewIssueService;

    /**
     * 審査コントローラを生成します。
     *
     * @param reviewService 審査記録サービス
     * @param reviewIssueService 審査指摘事項サービス
     */
    public ReviewController(final ReviewService reviewService, final ReviewIssueService reviewIssueService) {
        this.reviewService = reviewService;
        this.reviewIssueService = reviewIssueService;
    }

    /**
     * 指定された作品の審査記録を取得します。
     *
     * @param projectId 作品ID
     * @return 審査記録
     */
    @GetMapping("/reviews")
    public List<ReviewRecordResponse> listReviews(@PathVariable final UUID projectId) {
        return reviewService.findAll(projectId);
    }

    /**
     * 審査記録を作成します。
     *
     * @param projectId 作品ID
     * @param request 審査記録作成リクエスト
     * @return 作成した審査記録
     */
    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewRecordResponse addReview(@PathVariable final UUID projectId,
                                          @Valid @RequestBody final ReviewRecordCreateRequest request) {
        return reviewService.create(projectId, request);
    }

    /**
     * 審査記録を更新します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param request 審査記録更新リクエスト
     * @return 更新後の審査記録
     */
    @PatchMapping("/reviews/{reviewId}")
    public ReviewRecordResponse updateReview(@PathVariable final UUID projectId,
                                             @PathVariable final UUID reviewId,
                                             @Valid @RequestBody final ReviewRecordUpdateRequest request) {
        return reviewService.update(projectId, reviewId, request);
    }

    /**
     * 指定された審査記録の指摘事項を取得します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @return 審査指摘事項
     */
    @GetMapping("/reviews/{reviewId}/issues")
    public List<ReviewIssueResponse> listIssues(@PathVariable final UUID projectId,
                                                @PathVariable final UUID reviewId) {
        return reviewIssueService.findIssues(projectId, reviewId);
    }

    /**
     * 審査指摘事項を追加します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param request 審査指摘事項作成リクエスト
     * @return 追加した審査指摘事項
     */
    @PostMapping("/reviews/{reviewId}/issues")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewIssueResponse addIssue(@PathVariable final UUID projectId,
                                        @PathVariable final UUID reviewId,
                                        @Valid @RequestBody final ReviewIssueCreateRequest request) {
        return reviewIssueService.addIssue(projectId, reviewId, request);
    }

    /**
     * 審査指摘事項を更新します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param issueId 審査指摘事項ID
     * @param request 審査指摘事項更新リクエスト
     * @return 更新後の審査指摘事項
     */
    @PatchMapping("/reviews/{reviewId}/issues/{issueId}")
    public ReviewIssueResponse updateIssue(@PathVariable final UUID projectId,
                                           @PathVariable final UUID reviewId,
                                           @PathVariable final UUID issueId,
                                           @Valid @RequestBody final ReviewIssueUpdateRequest request) {
        return reviewIssueService.updateIssue(projectId, reviewId, issueId, request);
    }

    /**
     * 審査指摘事項を削除します。
     *
     * @param projectId 作品ID
     * @param reviewId 審査記録ID
     * @param issueId 審査指摘事項ID
     */
    @DeleteMapping("/reviews/{reviewId}/issues/{issueId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIssue(@PathVariable final UUID projectId,
                            @PathVariable final UUID reviewId,
                            @PathVariable final UUID issueId) {
        reviewIssueService.deleteIssue(projectId, reviewId, issueId);
    }
}
