package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * UZU公式ガイドの記事を表すエンティティです。
 */
@Entity
@Table(name = "guide_articles")
public class GuideArticle extends AuditedEntity {

    /**
     * 記事タイトル
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * ガイドカテゴリ
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private GuideCategory category;

    /**
     * 記事概要
     */
    @Column(length = 500)
    private String summary;

    /**
     * 記事本文
     */
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * 参照元URL
     */
    @Column(name = "source_url", length = 1000)
    private String sourceUrl;

    /**
     * 内容を確認した日
     */
    @Column(name = "checked_at")
    private LocalDate checkedAt;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected GuideArticle() {
    }

    /**
     * ガイド記事を生成します。
     *
     * @param id ガイド記事ID
     * @param title 記事タイトル
     * @param category ガイドカテゴリ
     * @param summary 記事概要
     * @param content 記事本文
     * @param sourceUrl 参照元URL
     * @param checkedAt 内容を確認した日
     */
    public GuideArticle(final UUID id, final String title, final GuideCategory category,
                        final String summary, final String content, final String sourceUrl,
                        final LocalDate checkedAt) {
        super(id);
        this.title = title;
        this.category = category;
        this.summary = summary;
        this.content = content;
        this.sourceUrl = sourceUrl;
        this.checkedAt = checkedAt;
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
     * 記事タイトルを設定します。
     *
     * @param title 記事タイトル
     */
    public void setTitle(final String title) {
        this.title = title;
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
     * ガイドカテゴリを設定します。
     *
     * @param category ガイドカテゴリ
     */
    public void setCategory(final GuideCategory category) {
        this.category = category;
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
     * 記事概要を設定します。
     *
     * @param summary 記事概要
     */
    public void setSummary(final String summary) {
        this.summary = summary;
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
     * 記事本文を設定します。
     *
     * @param content 記事本文
     */
    public void setContent(final String content) {
        this.content = content;
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
     * 参照元URLを設定します。
     *
     * @param sourceUrl 参照元URL
     */
    public void setSourceUrl(final String sourceUrl) {
        this.sourceUrl = sourceUrl;
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
     * 内容を確認した日を設定します。
     *
     * @param checkedAt 内容を確認した日
     */
    public void setCheckedAt(final LocalDate checkedAt) {
        this.checkedAt = checkedAt;
    }
}
