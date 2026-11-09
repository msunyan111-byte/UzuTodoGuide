package com.example.uzu.web.dto;

import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.domain.ReviewStatus;
import com.example.uzu.domain.ReviewType;
import com.example.uzu.support.TokyoTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 審査記録レスポンスを表すDTOです。
 */
public class ReviewRecordResponse {

    /**
     * 審査記録ID
     */
    private final UUID id;

    /**
     * 作品ID
     */
    private final UUID projectId;

    /**
     * 審査種別
     */
    private final ReviewType reviewType;

    /**
     * 提出日
     */
    private final LocalDate submittedAt;

    /**
     * 審査ステータス
     */
    private final ReviewStatus status;

    /**
     * 審査結果
     */
    private final String result;

    /**
     * メモ
     */
    private final String memo;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * 審査記録レスポンスを生成します。
     *
     * @param id 審査記録ID
     * @param projectId 作品ID
     * @param reviewType 審査種別
     * @param submittedAt 提出日
     * @param status 審査ステータス
     * @param result 審査結果
     * @param memo メモ
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public ReviewRecordResponse(final UUID id, final UUID projectId, final ReviewType reviewType,
                                final LocalDate submittedAt, final ReviewStatus status, final String result,
                                final String memo, final OffsetDateTime createdAt,
                                final OffsetDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.reviewType = reviewType;
        this.submittedAt = submittedAt;
        this.status = status;
        this.result = result;
        this.memo = memo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 審査記録エンティティからレスポンスを生成します。
     *
     * @param reviewRecord 審査記録エンティティ
     * @return 審査記録レスポンス
     */
    public static ReviewRecordResponse from(final ReviewRecord reviewRecord) {
        return new ReviewRecordResponse(
                reviewRecord.getId(),
                reviewRecord.getProjectId(),
                reviewRecord.getReviewType(),
                reviewRecord.getSubmittedAt(),
                reviewRecord.getStatus(),
                reviewRecord.getResult(),
                reviewRecord.getMemo(),
                TokyoTime.toOffset(reviewRecord.getCreatedAt()),
                TokyoTime.toOffset(reviewRecord.getUpdatedAt()));
    }

    /**
     * 審査記録IDを取得します。
     *
     * @return 審査記録ID
     */
    public UUID getId() {
        return id;
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
     * 提出日を取得します。
     *
     * @return 提出日
     */
    public LocalDate getSubmittedAt() {
        return submittedAt;
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
     * 審査結果を取得します。
     *
     * @return 審査結果
     */
    public String getResult() {
        return result;
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
