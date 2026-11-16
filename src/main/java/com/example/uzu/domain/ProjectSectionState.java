package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 作品の画面セクションごとのメモと最終確認日時を表すエンティティです。
 */
@Entity
@Table(name = "project_section_states",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_project_section_states",
                columnNames = {"project_id", "section_key"}))
public class ProjectSectionState extends AuditedEntity {

    /**
     * 作品ID
     */
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    /**
     * 画面セクションのキー
     */
    @Column(name = "section_key", nullable = false, length = 50)
    private String sectionKey;

    /**
     * セクションのメモ
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 最終確認日時
     */
    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected ProjectSectionState() {
    }

    /**
     * セクション状態を生成します。
     *
     * @param id セクション状態ID
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     */
    public ProjectSectionState(final UUID id, final UUID projectId, final String sectionKey) {
        super(id);
        this.projectId = projectId;
        this.sectionKey = sectionKey;
    }

    /**
     * 作品IDを取得します。
     *
     * @return 作品ID
     */
    public UUID getProjectId() {
        return projectId;
    }

    /**
     * 画面セクションのキーを取得します。
     *
     * @return 画面セクションのキー
     */
    public String getSectionKey() {
        return sectionKey;
    }

    /**
     * セクションのメモを取得します。
     *
     * @return セクションのメモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * セクションのメモを設定します。
     *
     * @param memo セクションのメモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }

    /**
     * 最終確認日時を取得します。
     *
     * @return 最終確認日時
     */
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    /**
     * 最終確認日時を設定します。
     *
     * @param verifiedAt 最終確認日時
     */
    public void setVerifiedAt(final LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}
