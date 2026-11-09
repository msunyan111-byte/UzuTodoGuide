package com.example.uzu.domain;

/**
 * ガイド記事のカテゴリを表す列挙型です。
 */
public enum GuideCategory {
    MAKING_FLOW("制作の流れ"),
    OVERVIEW("作品概要"),
    EDITOR("エディタ機能"),
    PLAYTEST("テストプレイ"),
    REVIEW("審査・公開"),
    FAQ("FAQ");

    /**
     * 表示用ラベル
     */
    private final String label;

    GuideCategory(final String label) {
        this.label = label;
    }

    /**
     * 表示用ラベルを取得します。
     *
     * @return 表示用ラベル
     */
    public String getLabel() {
        return label;
    }

    /**
     * コードまたはラベルからカテゴリを取得します。
     *
     * @param value カテゴリコードまたはラベル。nullまたは空白の場合はnullを返す
     * @return 一致するカテゴリ。見つからない場合はnull
     */
    public static GuideCategory fromValue(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final String normalized = value.trim();
        for (final GuideCategory category : values()) {
            if (category.name().equalsIgnoreCase(normalized)
                    || category.label.equals(normalized)
                    || category.label.equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        return null;
    }
}
