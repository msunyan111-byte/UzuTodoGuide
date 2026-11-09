package com.example.uzu.web.dto;

import com.example.uzu.domain.PlaytestPlatform;
import com.example.uzu.domain.PlaytestRecord;
import com.example.uzu.support.TokyoTime;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * テストプレイ記録レスポンスを表すDTOです。
 */
public class PlaytestRecordResponse {

    /**
     * テストプレイ記録ID
     */
    private final UUID id;

    /**
     * 作品ID
     */
    private final UUID projectId;

    /**
     * 実施日
     */
    private final LocalDate playedAt;

    /**
     * 実施場所
     */
    private final PlaytestPlatform platform;

    /**
     * 参加者数
     */
    private final Integer participantCount;

    /**
     * 実施したバージョンのメモ
     */
    private final String versionNote;

    /**
     * アンケートを回収済みの場合はtrue
     */
    private final boolean questionnaireCollected;

    /**
     * アンケートURL
     */
    private final String questionnaireUrl;

    /**
     * テストプレイで得られた気づき
     */
    private final String findings;

    /**
     * メモ
     */
    private final String memo;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * テストプレイ記録レスポンスを生成します。
     *
     * @param id テストプレイ記録ID
     * @param projectId 作品ID
     * @param playedAt 実施日
     * @param platform 実施場所
     * @param participantCount 参加者数
     * @param versionNote 実施したバージョンのメモ
     * @param questionnaireCollected アンケートを回収済みの場合はtrue
     * @param questionnaireUrl アンケートURL
     * @param findings テストプレイで得られた気づき
     * @param memo メモ
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public PlaytestRecordResponse(final UUID id, final UUID projectId, final LocalDate playedAt,
                                  final PlaytestPlatform platform, final Integer participantCount,
                                  final String versionNote, final boolean questionnaireCollected,
                                  final String questionnaireUrl, final String findings, final String memo,
                                  final OffsetDateTime createdAt, final OffsetDateTime updatedAt) {
        this.id = id;
        this.projectId = projectId;
        this.playedAt = playedAt;
        this.platform = platform;
        this.participantCount = participantCount;
        this.versionNote = versionNote;
        this.questionnaireCollected = questionnaireCollected;
        this.questionnaireUrl = questionnaireUrl;
        this.findings = findings;
        this.memo = memo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * テストプレイ記録エンティティからレスポンスを生成します。
     *
     * @param record テストプレイ記録エンティティ
     * @return テストプレイ記録レスポンス
     */
    public static PlaytestRecordResponse from(final PlaytestRecord record) {
        return new PlaytestRecordResponse(
                record.getId(),
                record.getProjectId(),
                record.getPlayedAt(),
                record.getPlatform(),
                record.getParticipantCount(),
                record.getVersionNote(),
                record.isQuestionnaireCollected(),
                record.getQuestionnaireUrl(),
                record.getFindings(),
                record.getMemo(),
                TokyoTime.toOffset(record.getCreatedAt()),
                TokyoTime.toOffset(record.getUpdatedAt()));
    }

    /**
     * テストプレイ記録IDを取得します。
     *
     * @return テストプレイ記録ID
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
     * 実施日を取得します。
     *
     * @return 実施日
     */
    public LocalDate getPlayedAt() {
        return playedAt;
    }

    /**
     * 実施場所を取得します。
     *
     * @return 実施場所
     */
    public PlaytestPlatform getPlatform() {
        return platform;
    }

    /**
     * 参加者数を取得します。
     *
     * @return 参加者数
     */
    public Integer getParticipantCount() {
        return participantCount;
    }

    /**
     * 実施したバージョンのメモを取得します。
     *
     * @return 実施したバージョンのメモ
     */
    public String getVersionNote() {
        return versionNote;
    }

    /**
     * アンケートを回収済みかどうかを取得します。
     *
     * @return 回収済みの場合はtrue
     */
    public boolean isQuestionnaireCollected() {
        return questionnaireCollected;
    }

    /**
     * アンケートURLを取得します。
     *
     * @return アンケートURL
     */
    public String getQuestionnaireUrl() {
        return questionnaireUrl;
    }

    /**
     * テストプレイで得られた気づきを取得します。
     *
     * @return テストプレイで得られた気づき
     */
    public String getFindings() {
        return findings;
    }

    /**
     * メモを取得します。
     *
     * @return メモ
     */
    public String getMemo() {
        return memo;
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
