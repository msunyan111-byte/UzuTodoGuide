package com.example.uzu.web.dto;

import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewIssueStatus;
import com.example.uzu.support.TokyoTime;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 審査指摘事項レスポンスを表すDTOです。
 */
public class ReviewIssueResponse {

    /**
     * 審査指摘事項ID
     */
    private final UUID id;

    /**
     * 審査記録ID
     */
    private final UUID reviewId;

    /**
     * 指摘内容
     */
    private final String content;

    /**
     * 対応ステータス
     */
    private final ReviewIssueStatus status;

    /**
     * 対応メモ
     */
    private final String responseMemo;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * 審査指摘事項レスポンスを生成します。
     *
     * @param id 審査指摘事項ID
     * @param reviewId 審査記録ID
     * @param content 指摘内容
     * @param status 対応ステータス
     * @param responseMemo 対応メモ
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public ReviewIssueResponse(final UUID id, final UUID reviewId, final String content,
                               final ReviewIssueStatus status, final String responseMemo,
                               final OffsetDateTime createdAt, final OffsetDateTime updatedAt) {
        this.id = id;
        this.reviewId = reviewId;
        this.content = content;
        this.status = status;
        this.responseMemo = responseMemo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 審査指摘事項エンティティからレスポンスを生成します。
     *
     * @param issue 審査指摘事項エンティティ
     * @return 審査指摘事項レスポンス
     */
    public static ReviewIssueResponse from(final ReviewIssue issue) {
        return new ReviewIssueResponse(
                issue.getId(),
                issue.getReviewId(),
                issue.getContent(),
                issue.getStatus(),
                issue.getResponseMemo(),
                TokyoTime.toOffset(issue.getCreatedAt()),
                TokyoTime.toOffset(issue.getUpdatedAt()));
    }

    /**
     * 審査指摘事項IDを取得します。
     *
     * @return 審査指摘事項ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 審査記録IDを取得します。
     *
     * @return 審査記録ID
     */
    public UUID getReviewId() {
        return reviewId;
    }

    /**
     * 指摘内容を取得します。
     *
     * @return 指摘内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 対応ステータスを取得します。
     *
     * @return 対応ステータス
     */
    public ReviewIssueStatus getStatus() {
        return status;
    }

    /**
     * 対応メモを取得します。
     *
     * @return 対応メモ
     */
    public String getResponseMemo() {
        return responseMemo;
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新日時を取得します。
     *
     * @return 更新日時
     */
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
