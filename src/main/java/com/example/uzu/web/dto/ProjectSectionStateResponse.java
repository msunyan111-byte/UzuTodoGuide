package com.example.uzu.web.dto;

import com.example.uzu.domain.ProjectSectionState;
import com.example.uzu.support.TokyoTime;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 画面セクション状態のレスポンスを表すDTOです。
 */
public class ProjectSectionStateResponse {

    /**
     * セクション状態ID
     */
    private final UUID id;

    /**
     * 作品ID
     */
    private final UUID projectId;

    /**
     * 画面セクションのキー
     */
    private final String sectionKey;

    /**
     * セクションのメモ
     */
    private final String memo;

    /**
     * 最終確認日時
     */
    private final OffsetDateTime verifiedAt;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * 画面セクション状態のレスポンスを生成します。
     *
     * @param id セクション状態ID
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @param memo セクションのメモ
     * @param verifiedAt 最終確認日時
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public ProjectSectionStateResponse(final UUID id, final UUID projectId, final String sectionKey,
                                       final String memo, final OffsetDateTime verifiedAt,
                                       final OffsetDateTime createdAt, final OffsetDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.sectionKey = sectionKey;
        this.memo = memo;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * セクション状態エンティティからレスポンスを生成します。
     *
     * @param state セクション状態エンティティ
     * @return セクション状態レスポンス
     */
    public static ProjectSectionStateResponse from(final ProjectSectionState state) {
        return new ProjectSectionStateResponse(
                state.getId(),
                state.getProjectId(),
                state.getSectionKey(),
                state.getMemo(),
                TokyoTime.toOffset(state.getVerifiedAt()),
                TokyoTime.toOffset(state.getCreatedAt()),
                TokyoTime.toOffset(state.getUpdatedAt()));
    }

    /**
     * セクション状態IDを取得します。
     *
     * @return セクション状態ID
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
     * 最終確認日時を取得します。
     *
     * @return 最終確認日時
     */
    public OffsetDateTime getVerifiedAt() {
        return verifiedAt;
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
