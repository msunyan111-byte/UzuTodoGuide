package com.example.uzu.web.dto;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.support.TokyoTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * チェックリスト項目レスポンスを表すDTOです。
 */
public class ChecklistItemResponse {

    /**
     * チェックリスト項目ID
     */
    private final UUID id;

    /**
     * 作品ID
     */
    private final UUID projectId;

    /**
     * 項目名
     */
    private final String name;

    /**
     * 項目の説明
     */
    private final String description;

    /**
     * 対応状態
     */
    private final ChecklistStatus status;

    /**
     * 項目メモ
     */
    private final String memo;

    /**
     * 期限日
     */
    private final LocalDate dueDate;

    /**
     * 表示順
     */
    private final int displayOrder;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * チェックリスト項目レスポンスを生成します。
     *
     * @param id チェックリスト項目ID
     * @param projectId 作品ID
     * @param name 項目名
     * @param description 項目の説明
     * @param status 対応状態
     * @param memo 項目メモ
     * @param dueDate 期限日
     * @param displayOrder 表示順
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public ChecklistItemResponse(final UUID id, final UUID projectId, final String name,
                                 final String description, final ChecklistStatus status, final String memo,
                                 final LocalDate dueDate, final int displayOrder,
                                 final OffsetDateTime createdAt, final OffsetDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.status = status;
        this.memo = memo;
        this.dueDate = dueDate;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * チェックリスト項目エンティティからレスポンスを生成します。
     *
     * @param item チェックリスト項目エンティティ
     * @return チェックリスト項目レスポンス
     */
    public static ChecklistItemResponse from(final ChecklistItem item) {
        return new ChecklistItemResponse(
                item.getId(),
                item.getProjectId(),
                item.getName(),
                item.getDescription(),
                item.getStatus(),
                item.getMemo(),
                item.getDueDate(),
                item.getDisplayOrder(),
                TokyoTime.toOffset(item.getCreatedAt()),
                TokyoTime.toOffset(item.getUpdatedAt()));
    }

    /**
     * チェックリスト項目IDを取得します。
     *
     * @return チェックリスト項目ID
     */
    public UUID getId() {
        return id;
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
     * 項目の説明を取得します。
     *
     * @return 項目の説明
     */
    public String getDescription() {
        return description;
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
     * 項目メモを取得します。
     *
     * @return 項目メモ
     */
    public String getMemo() {
        return memo;
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
     * 表示順を取得します。
     *
     * @return 表示順
     */
    public int getDisplayOrder() {
        return displayOrder;
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
