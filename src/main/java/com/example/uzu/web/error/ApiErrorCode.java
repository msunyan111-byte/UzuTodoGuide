package com.example.uzu.web.error;

/**
 * APIエラーレスポンスで使用するエラーコードです。
 */
public enum ApiErrorCode {

    /**
     * 入力内容の検証エラー
     */
    VALIDATION_ERROR,

    /**
     * リソース未検出
     */
    NOT_FOUND,

    /**
     * HTTPメソッド非対応
     */
    METHOD_NOT_ALLOWED,

    /**
     * メディアタイプ非対応
     */
    UNSUPPORTED_MEDIA_TYPE,

    /**
     * サーバー内部エラー
     */
    INTERNAL_ERROR
}
