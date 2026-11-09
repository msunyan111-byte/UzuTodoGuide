package com.example.uzu.web.dto;

import com.example.uzu.domain.ReviewIssueStatus;

/**
 * 審査指摘事項更新リクエストを表すDTOです。
 */
public class ReviewIssueUpdateRequest {

    /**
     * 指摘内容
     */
    private String content;

    /**
     * 対応ステータス
     */
    private ReviewIssueStatus status;

    /**
     * 対応メモ
     */
    private String responseMemo;

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
