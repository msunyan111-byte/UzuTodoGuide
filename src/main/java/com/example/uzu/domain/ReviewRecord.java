package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 審査記録を表すエンティティです。
 */
@Entity
@Table(name = "review_records")
public class ReviewRecord extends AuditedEntity {

    /**
     * 作品ID
     */
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    /**
     * 審査種別
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "review_type", nullable = false, length = 20)
    private ReviewType reviewType;

    /**
     * 提出日
     */
    @Column(name = "submitted_at")
    private LocalDate submittedAt;

    /**
     * 審査ステータス
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReviewStatus status;

    /**
     * 審査結果
     */
    @Column(columnDefinition = "TEXT")
    private String result;

    /**
     * メモ
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected ReviewRecord() {
    }

    /**
     * 審査記録を生成します。
     *
     * @param id 審査記録ID
     * @param projectId 作品ID
     * @param reviewType 審査種別
     */
    public ReviewRecord(final UUID id, final UUID projectId, final ReviewType reviewType) {
        super(id);
        this.projectId = projectId;
        this.reviewType = reviewType;
        this.status = ReviewStatus.DRAFT;
    }

    /**
     * 作品IDを取得します。
     *
     * @return 作品ID
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * 審査種別を取得します。
     *
     * @return 審査種別
     */
    public ReviewType getReviewType() {
        return reviewType;
    }

    /**
     * 審査種別を設定します。
     *
     * @param reviewType 審査種別
     */
    public void setReviewType(final ReviewType reviewType) {
        this.reviewType = reviewType;
    }

    /**
     * 提出日を取得します。
     *
     * @return 提出日
     */
    public LocalDate getSubmittedAt() {
        return submittedAt;
    }

    /**
     * 提出日を設定します。
     *
     * @param submittedAt 提出日
     */
    public void setSubmittedAt(final LocalDate submittedAt) {
        this.submittedAt = submittedAt;
    }

    /**
     * 審査ステータスを取得します。
     *
     * @return 審査ステータス
     */
    public ReviewStatus getStatus() {
        return status;
    }

    /**
     * 審査ステータスを設定します。
     *
     * @param status 審査ステータス
     */
    public void setStatus(final ReviewStatus status) {
        this.status = status;
    }

    /**
     * 審査結果を取得します。
     *
     * @return 審査結果
     */
    public String getResult() {
        return result;
    }

    /**
     * 審査結果を設定します。
     *
     * @param result 審査結果
     */
    public void setResult(final String result) {
        this.result = result;
    }

    /**
     * メモを取得します。
     *
     * @return メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * メモを設定します。
     *
     * @param memo メモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }
}
