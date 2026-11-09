package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 審査指摘事項を表すエンティティです。
 */
@Entity
@Table(name = "review_issues")
public class ReviewIssue extends AuditedEntity {

    /**
     * 審査記録ID
     */
    @Column(name = "review_id", nullable = false, updatable = false)
    private UUID reviewId;

    /**
     * 指摘内容
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 対応ステータス
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReviewIssueStatus status;

    /**
     * 対応メモ
     */
    @Column(name = "response_memo", columnDefinition = "TEXT")
    private String responseMemo;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected ReviewIssue() {
    }

    /**
     * 審査指摘事項を生成します。
     *
     * @param id 審査指摘事項ID
     * @param reviewId 審査記録ID
     * @param content 指摘内容
     */
    public ReviewIssue(final UUID id, final UUID reviewId, final String content) {
        super(id);
        this.reviewId = reviewId;
        this.content = content;
        this.status = ReviewIssueStatus.OPEN;
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
     * 指摘内容を設定します。
     *
     * @param content 指摘内容
     */
    public void setContent(final String content) {
        this.content = content;
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
     * 対応ステータスを設定します。
     *
     * @param status 対応ステータス
     */
    public void setStatus(final ReviewIssueStatus status) {
        this.status = status;
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
     * 対応メモを設定します。
     *
     * @param responseMemo 対応メモ
     */
    public void setResponseMemo(final String responseMemo) {
        this.responseMemo = responseMemo;
    }
}
