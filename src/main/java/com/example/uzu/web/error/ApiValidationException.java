package com.example.uzu.web.error;

import java.util.List;

/**
 * APIの入力検証エラーを表す例外クラスです。
 */
public class ApiValidationException extends RuntimeException {

    /**
     * 入力エラーの詳細一覧
     */
    private final List<ErrorDetail> details;

    /**
     * 入力検証エラーを生成します。
     *
     * @param details 入力エラーの詳細一覧
     */
    public ApiValidationException(final List<ErrorDetail> details) {
        super("入力内容を確認してください。");
        this.details = details == null ? List.of() : List.copyOf(details);
    }

    /**
     * 単一の入力エラーを生成します。
     *
     * @param field エラー対象のフィールド名
     * @param reason エラー理由
     * @return 入力検証エラー
     */
    public static ApiValidationException of(final String field, final String reason) {
        return new ApiValidationException(List.of(new ErrorDetail(field, reason)));
    }

    /**
     * 入力エラーの詳細一覧を取得します。
     *
     * @return 入力エラーの詳細一覧
     */
    public List<ErrorDetail> getDetails() {
        return details;
    }
}
