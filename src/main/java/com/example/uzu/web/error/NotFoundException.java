package com.example.uzu.web.error;

/**
 * 対象のリソースが見つからないことを表す例外クラスです。
 */
public class NotFoundException extends RuntimeException {

    /**
     * リソース未検出例外を生成します。
     *
     * @param message エラーメッセージ
     */
    public NotFoundException(final String message) {
        super(message);
    }
}
