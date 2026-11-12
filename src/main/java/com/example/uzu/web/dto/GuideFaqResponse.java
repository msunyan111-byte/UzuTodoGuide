package com.example.uzu.web.dto;

import com.example.uzu.domain.GuideFaq;
import com.example.uzu.support.TokyoTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * UZU機能ガイドのQ&Aレスポンスを表すDTOです。
 */
public class GuideFaqResponse {

    /**
     * Q&AのID
     */
    private final UUID id;

    /**
     * 質問文
     */
    private final String question;

    /**
     * 回答文
     */
    private final String answer;

    /**
     * Q&Aカテゴリのコード
     */
    private final String category;

    /**
     * Q&Aカテゴリの表示ラベル
     */
    private final String categoryLabel;

    /**
     * 関連するガイドセクションのキー
     */
    private final String sectionKey;

    /**
     * 参照元URL
     */
    private final String sourceUrl;

    /**
     * 内容を確認した日
     */
    private final LocalDate checkedAt;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * Q&Aレスポンスを生成します。
     *
     * @param id Q&AのID
     * @param question 質問文
     * @param answer 回答文
     * @param category Q&Aカテゴリのコード
     * @param categoryLabel Q&Aカテゴリの表示ラベル
     * @param sectionKey 関連するガイドセクションのキー
     * @param sourceUrl 参照元URL
     * @param checkedAt 内容を確認した日
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public GuideFaqResponse(final UUID id, final String question, final String answer,
                            final String category, final String categoryLabel, final String sectionKey,
                            final String sourceUrl, final LocalDate checkedAt,
                            final OffsetDateTime createdAt, final OffsetDateTime updatedAt) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.categoryLabel = categoryLabel;
        this.sectionKey = sectionKey;
        this.sourceUrl = sourceUrl;
        this.checkedAt = checkedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Q&Aエンティティからレスポンスを生成します。
     *
     * @param faq Q&Aエンティティ
     * @return Q&Aレスポンス
     */
    public static GuideFaqResponse from(final GuideFaq faq) {
        return new GuideFaqResponse(
                faq.getId(),
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory().name(),
                faq.getCategory().getLabel(),
                faq.getSectionKey(),
                faq.getSourceUrl(),
                faq.getCheckedAt(),
                TokyoTime.toOffset(faq.getCreatedAt()),
                TokyoTime.toOffset(faq.getUpdatedAt()));
    }

    /**
     * Q&AのIDを取得します。
     *
     * @return Q&AのID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 質問文を取得します。
     *
     * @return 質問文
     */
    public String getQuestion() {
        return question;
    }

    /**
     * 回答文を取得します。
     *
     * @return 回答文
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * Q&Aカテゴリのコードを取得します。
     *
     * @return Q&Aカテゴリのコード
     */
    public String getCategory() {
        return category;
    }

    /**
     * Q&Aカテゴリの表示ラベルを取得します。
     *
     * @return Q&Aカテゴリの表示ラベル
     */
    public String getCategoryLabel() {
        return categoryLabel;
    }

    /**
     * 関連するガイドセクションのキーを取得します。
     *
     * @return 関連するガイドセクションのキー
     */
    public String getSectionKey() {
        return sectionKey;
    }

    /**
     * 参照元URLを取得します。
     *
     * @return 参照元URL
     */
    public String getSourceUrl() {
        return sourceUrl;
    }

    /**
     * 内容を確認した日を取得します。
     *
     * @return 内容を確認した日
     */
    public LocalDate getCheckedAt() {
        return checkedAt;
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新日時を取得します。
     *
     * @return 更新日時
     */
    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
