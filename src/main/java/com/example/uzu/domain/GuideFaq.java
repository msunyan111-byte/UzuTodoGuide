package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * UZU機能ガイドの困ったときのQ&Aを表すエンティティです。
 */
@Entity
@Table(name = "guide_faqs")
public class GuideFaq extends AuditedEntity {

    /**
     * 質問文
     */
    @Column(nullable = false, length = 300)
    private String question;

    /**
     * 回答文
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    /**
     * Q&Aカテゴリ
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private GuideFaqCategory category;

    /**
     * 関連するガイドセクションのキー
     */
    @Column(name = "section_key", length = 40)
    private String sectionKey;

    /**
     * 参照元URL
     */
    @Column(name = "source_url", nullable = false, length = 1000)
    private String sourceUrl;

    /**
     * 内容を確認した日
     */
    @Column(name = "checked_at", nullable = false)
    private LocalDate checkedAt;

    /**
     * 表示順
     */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    /**
     * 表示対象かどうか
     */
    @Column(nullable = false)
    private boolean published;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected GuideFaq() {
    }

    /**
     * Q&Aを生成します。
     *
     * @param id Q&AのID
     * @param question 質問文
     * @param answer 回答文
     * @param category Q&Aカテゴリ
     * @param sectionKey 関連するガイドセクションのキー
     * @param sourceUrl 参照元URL
     * @param checkedAt 内容を確認した日
     * @param displayOrder 表示順
     * @param published 表示対象かどうか
     */
    public GuideFaq(final UUID id, final String question, final String answer,
                    final GuideFaqCategory category, final String sectionKey, final String sourceUrl,
                    final LocalDate checkedAt, final int displayOrder, final boolean published) {
        super(id);
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.sectionKey = sectionKey;
        this.sourceUrl = sourceUrl;
        this.checkedAt = checkedAt;
        this.displayOrder = displayOrder;
        this.published = published;
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
     * 質問文を設定します。
     *
     * @param question 質問文
     */
    public void setQuestion(final String question) {
        this.question = question;
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
     * 回答文を設定します。
     *
     * @param answer 回答文
     */
    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    /**
     * Q&Aカテゴリを取得します。
     *
     * @return Q&Aカテゴリ
     */
    public GuideFaqCategory getCategory() {
        return category;
    }

    /**
     * Q&Aカテゴリを設定します。
     *
     * @param category Q&Aカテゴリ
     */
    public void setCategory(final GuideFaqCategory category) {
        this.category = category;
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
     * 関連するガイドセクションのキーを設定します。
     *
     * @param sectionKey 関連するガイドセクションのキー
     */
    public void setSectionKey(final String sectionKey) {
        this.sectionKey = sectionKey;
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

    /**
     * 表示順を取得します。
     *
     * @return 表示順
     */
    public int getDisplayOrder() {
        return displayOrder;
    }

    /**
     * 表示順を設定します。
     *
     * @param displayOrder 表示順
     */
    public void setDisplayOrder(final int displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * 表示対象かどうかを取得します。
     *
     * @return 表示対象の場合はtrue
     */
    public boolean isPublished() {
        return published;
    }

    /**
     * 表示対象かどうかを設定します。
     *
     * @param published 表示対象の場合はtrue
     */
    public void setPublished(final boolean published) {
        this.published = published;
    }
}
