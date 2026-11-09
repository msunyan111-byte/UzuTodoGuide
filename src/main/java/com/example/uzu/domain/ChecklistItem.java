package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * 作品のチェックリスト項目を表すエンティティです。
 */
@Entity
@Table(name = "checklist_items")
public class ChecklistItem extends AuditedEntity {

    /**
     * 作品ID
     */
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    /**
     * 項目名
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * 項目の説明
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 対応状態
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ChecklistStatus status;

    /**
     * 項目メモ
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * 期限日
     */
    @Column(name = "due_date")
    private LocalDate dueDate;

    /**
     * 表示順
     */
    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected ChecklistItem() {
    }

    /**
     * チェックリスト項目を生成します。
     *
     * @param id チェックリスト項目ID
     * @param projectId 作品ID
     * @param name 項目名
     * @param displayOrder 表示順
     */
    public ChecklistItem(final UUID id, final UUID projectId, final String name, final int displayOrder) {
        super(id);
        this.projectId = projectId;
        this.name = name;
        this.displayOrder = displayOrder;
        this.status = ChecklistStatus.NOT_STARTED;
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
     * 項目名を取得します。
     *
     * @return 項目名
     */
    public String getName() {
        return name;
    }

    /**
     * 項目名を設定します。
     *
     * @param name 項目名
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * 項目の説明を取得します。
     *
     * @return 項目の説明
     */
    public String getDescription() {
        return description;
    }

    /**
     * 項目の説明を設定します。
     *
     * @param description 項目の説明
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * 対応状態を取得します。
     *
     * @return 対応状態
     */
    public ChecklistStatus getStatus() {
        return status;
    }

    /**
     * 対応状態を設定します。
     *
     * @param status 対応状態
     */
    public void setStatus(final ChecklistStatus status) {
        this.status = status;
    }

    /**
     * 項目メモを取得します。
     *
     * @return 項目メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * 項目メモを設定します。
     *
     * @param memo 項目メモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }

    /**
     * 期限日を取得します。
     *
     * @return 期限日
     */
    public LocalDate getDueDate() {
        return dueDate;
    }

    /**
     * 期限日を設定します。
     *
     * @param dueDate 期限日
     */
    public void setDueDate(final LocalDate dueDate) {
        this.dueDate = dueDate;
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
}
