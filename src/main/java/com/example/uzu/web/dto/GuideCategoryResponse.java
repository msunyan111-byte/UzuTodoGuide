package com.example.uzu.web.dto;

import com.example.uzu.domain.GuideCategory;

/**
 * ガイドカテゴリレスポンスを表すDTOです。
 */
public class GuideCategoryResponse {

    /**
     * カテゴリコード
     */
    private final String code;

    /**
     * 表示用ラベル
     */
    private final String label;

    /**
     * ガイドカテゴリレスポンスを生成します。
     *
     * @param code カテゴリコード
     * @param label 表示用ラベル
     */
    public GuideCategoryResponse(final String code, final String label) {
        this.code = code;
        this.label = label;
    }

    /**
     * ガイドカテゴリからレスポンスを生成します。
     *
     * @param category ガイドカテゴリ
     * @return ガイドカテゴリレスポンス
     */
    public static GuideCategoryResponse from(final GuideCategory category) {
        return new GuideCategoryResponse(category.name(), category.getLabel());
    }

    /**
     * カテゴリコードを取得します。
     *
     * @return カテゴリコード
     */
    public String getCode() {
        return code;
    }

    /**
     * 表示用ラベルを取得します。
     *
     * @return 表示用ラベル
     */
    public String getLabel() {
        return label;
    }
}
