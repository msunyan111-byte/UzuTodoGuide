package com.example.uzu.web.dto;

import javax.validation.constraints.Size;
import java.time.OffsetDateTime;

/**
 * 画面セクション状態の更新リクエストを表すDTOです。
 */
public class ProjectSectionStateUpdateRequest {

    /**
     * セクションのメモ
     */
    @Size(max = 2000)
    private String memo;

    /**
     * 最終確認日時
     */
    private OffsetDateTime verifiedAt;

    /**
     * セクションのメモを取得します。
     *
     * @return セクションのメモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * セクションのメモを設定します。
     *
     * @param memo セクションのメモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }

    /**
     * 最終確認日時を取得します。
     *
     * @return 最終確認日時
     */
    public OffsetDateTime getVerifiedAt() {
        return verifiedAt;
    }

    /**
     * 最終確認日時を設定します。
     *
     * @param verifiedAt 最終確認日時
     */
    public void setVerifiedAt(final OffsetDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
