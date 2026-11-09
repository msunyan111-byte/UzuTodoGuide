package com.example.uzu.service;

import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;

import java.util.UUID;

/**
 * サービス層で共通利用する入力検証を提供するクラスです。
 */
final class ServiceValidation {

    private ServiceValidation() {
    }

    /**
     * 必須文字列を検証し、前後の空白を除去した値を返します。
     *
     * @param value 検証対象の値
     * @param field エラー対象のフィールド名
     * @param message エラーメッセージ
     * @return 前後の空白を除去した値
     * @throws ApiValidationException 値がnullまたは空白のみの場合
     */
    static String requireText(final String value, final String field, final String message) {
        if (value == null || value.isBlank()) {
            throw ApiValidationException.of(field, message);
        }
        return value.trim();
    }

    /**
     * 必須値を検証して返します。
     *
     * @param value 検証対象の値
     * @param field エラー対象のフィールド名
     * @param message エラーメッセージ
     * @param <T> 値の型
     * @return 検証済みの値
     * @throws ApiValidationException 値がnullの場合
     */
    static <T> T requireValue(final T value, final String field, final String message) {
        if (value == null) {
            throw ApiValidationException.of(field, message);
        }
        return value;
    }

    /**
     * 指定された作品が存在することを検証します。
     *
     * @param projectRepository 作品リポジトリ
     * @param projectId 作品ID
     * @throws NotFoundException 作品が見つからない場合
     */
    static void requireProjectExists(final ProjectRepository projectRepository, final UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new NotFoundException("作品が見つかりません。");
        }
    }
}
