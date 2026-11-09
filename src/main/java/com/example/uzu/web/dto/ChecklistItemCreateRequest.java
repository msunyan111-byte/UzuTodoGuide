package com.example.uzu.web.dto;

import com.example.uzu.domain.ChecklistStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * チェックリスト項目作成リクエストを表すDTOです。
 */
public class ChecklistItemCreateRequest {

    /**
     * 項目名
     */
    @NotBlank
    @Size(max = 200)
    private String name;

    /**
     * 項目の説明
     */
    @Size(max = 10000)
    private String description;

    /**
     * 対応状態
     */
    private ChecklistStatus status;

    /**
     * 項目メモ
     */
    @Size(max = 10000)
    private String memo;

    /**
     * 期限日
     */
    private LocalDate dueDate;

    /**
     * 表示順
     */
    @Positive
    private Integer displayOrder;

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
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * 表示順を設定します。
     *
     * @param displayOrder 表示順
     */
    public void setDisplayOrder(final Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
