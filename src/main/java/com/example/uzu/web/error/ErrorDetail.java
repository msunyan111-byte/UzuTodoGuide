package com.example.uzu.web.error;

/**
 * APIの入力エラー詳細を表すクラスです。
 */
public class ErrorDetail {

    /**
     * エラー対象のフィールド名
     */
    private final String field;

    /**
     * エラー理由
     */
    private final String reason;

    /**
     * エラー詳細を生成します。
     *
     * @param field エラー対象のフィールド名
     * @param reason エラー理由
     */
    public ErrorDetail(final String field, final String reason) {
        this.field = field;
        this.reason = reason;
    }

    /**
     * エラー対象のフィールド名を取得します。
     *
     * @return エラー対象のフィールド名
     */
    public String getField() {
        return field;
    }

    /**
     * エラー理由を取得します。
     *
     * @return エラー理由
     */
    public String getReason() {
        return reason;
    }
}
