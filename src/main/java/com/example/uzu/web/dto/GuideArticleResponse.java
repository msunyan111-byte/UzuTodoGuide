package com.example.uzu.web.dto;

import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.GuideCategory;
import com.example.uzu.support.TokyoTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * ガイド記事レスポンスを表すDTOです。
 */
public class GuideArticleResponse {

    /**
     * ガイド記事ID
     */
    private final UUID id;

    /**
     * 記事タイトル
     */
    private final String title;

    /**
     * ガイドカテゴリ
     */
    private final GuideCategory category;

    /**
     * 記事概要
     */
    private final String summary;

    /**
     * 記事本文
     */
    private final String content;

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
     * ガイド記事レスポンスを生成します。
     *
     * @param id ガイド記事ID
     * @param title 記事タイトル
     * @param category ガイドカテゴリ
     * @param summary 記事概要
     * @param content 記事本文
     * @param sourceUrl 参照元URL
     * @param checkedAt 内容を確認した日
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public GuideArticleResponse(final UUID id, final String title, final GuideCategory category,
                                final String summary, final String content, final String sourceUrl,
                                final LocalDate checkedAt, final OffsetDateTime createdAt,
                                final OffsetDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.checkedAt = checkedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * ガイド記事エンティティからレスポンスを生成します。
     *
     * @param article ガイド記事エンティティ
     * @return ガイド記事レスポンス
     */
    public static GuideArticleResponse from(final GuideArticle article) {
        return new GuideArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getCategory(),
                article.getSummary(),
                article.getContent(),
                article.getSourceUrl(),
                article.getCheckedAt(),
                TokyoTime.toOffset(article.getCreatedAt()),
                TokyoTime.toOffset(article.getUpdatedAt()));
    }

    /**
     * ガイド記事IDを取得します。
     *
     * @return ガイド記事ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 記事タイトルを取得します。
     *
     * @return 記事タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * ガイドカテゴリを取得します。
     *
     * @return ガイドカテゴリ
     */
    public GuideCategory getCategory() {
        return category;
    }

    /**
     * 記事概要を取得します。
     *
     * @return 記事概要
     */
    public String getSummary() {
        return summary;
    }

    /**
     * 記事本文を取得します。
     *
     * @return 記事本文
     */
    public String getContent() {
        return content;
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
