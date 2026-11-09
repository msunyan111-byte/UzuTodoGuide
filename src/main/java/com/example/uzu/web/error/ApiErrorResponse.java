package com.example.uzu.web.error;

import java.util.List;

/**
 * APIの共通エラーレスポンスを表すクラスです。
 */
public class ApiErrorResponse {

    /**
     * エラー本体
     */
    private final ErrorBody error;

    private ApiErrorResponse(final ErrorBody error) {
        this.error = error;
    }

    /**
     * エラーレスポンスを生成します。
     *
     * @param code エラーコード
     * @param message エラーメッセージ
     * @param details エラー詳細一覧。nullの場合は空一覧として扱う
     * @return エラーレスポンス
     */
    public static ApiErrorResponse of(final String code, final String message, final List<ErrorDetail> details) {
        final List<ErrorDetail> safeDetails = details == null ? List.of() : List.copyOf(details);
        return new ApiErrorResponse(new ErrorBody(code, message, safeDetails));
    }

    /**
     * エラー本体を取得します。
     *
     * @return エラー本体
     */
    public ErrorBody getError() {
        return error;
    }

    /**
     * APIエラーの本体を表すクラスです。
     */
    public static final class ErrorBody {

        /**
         * エラーコード
         */
        private final String code;

        /**
         * エラーメッセージ
         */
        private final String message;

        /**
         * エラー詳細一覧
         */
        private final List<ErrorDetail> details;

        private ErrorBody(final String code, final String message, final List<ErrorDetail> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        /**
         * エラーコードを取得します。
         *
         * @return エラーコード
         */
        public String getCode() {
            return code;
        }

        /**
         * エラーメッセージを取得します。
         *
         * @return エラーメッセージ
         */
        public String getMessage() {
            return message;
        }

        /**
         * エラー詳細一覧を取得します。
         *
         * @return エラー詳細一覧
         */
        public List<ErrorDetail> getDetails() {
            return details;
        }
    }
}
