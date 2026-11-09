package com.example.uzu.web.dto;

import com.example.uzu.domain.ReviewStatus;
import com.example.uzu.domain.ReviewType;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 審査記録作成リクエストを表すDTOです。
 */
public class ReviewRecordCreateRequest {

    /**
     * 審査種別
     */
    @NotNull
    private ReviewType reviewType;

    /**
     * 提出日
     */
    private LocalDate submittedAt;

    /**
     * 審査ステータス
     */
    private ReviewStatus status;

    /**
     * 審査結果
     */
    private String result;

    /**
     * メモ
     */
    private String memo;

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
