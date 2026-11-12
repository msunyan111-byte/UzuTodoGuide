package com.example.uzu.domain;

/**
 * UZU機能ガイドの困ったときのQ&Aカテゴリを表す列挙型です。
 */
public enum GuideFaqCategory {
    CHARACTER("キャラクター"),
    PHASE("フェーズ・進行"),
    TEXT("テキスト"),
    MATERIAL("素材・アップロード"),
    CHECK("動作確認"),
    RELEASE("審査・公開"),
    MIGRATION("v1→v2移行"),
    OTHER("その他");

    /**
     * 表示用ラベル
     */
    private final String label;

    GuideFaqCategory(final String label) {
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
    public static GuideFaqCategory fromValue(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        final String normalized = value.trim();
        for (final GuideFaqCategory category : values()) {
            if (category.name().equalsIgnoreCase(normalized)
                    || category.label.equals(normalized)
                    || category.label.equalsIgnoreCase(normalized)) {
                return category;
            }
        }
        return null;
    }
}
