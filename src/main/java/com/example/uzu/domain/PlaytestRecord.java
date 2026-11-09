package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

/**
 * テストプレイ記録を表すエンティティです。
 */
@Entity
@Table(name = "playtest_records")
public class PlaytestRecord extends AuditedEntity {

    /**
     * 作品ID
     */
    @Column(name = "project_id", nullable = false, updatable = false)
    private UUID projectId;

    /**
     * 実施日
     */
    @Column(name = "played_at", nullable = false)
    private LocalDate playedAt;

    /**
     * 実施場所
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlaytestPlatform platform;

    /**
     * 参加者数
     */
    @Column(name = "participant_count")
    private Integer participantCount;

    /**
     * 実施したバージョンのメモ
     */
    @Column(name = "version_note", length = 200)
    private String versionNote;

    /**
     * アンケートを回収済みの場合はtrue
     */
    @Column(name = "questionnaire_collected", nullable = false)
    private boolean questionnaireCollected;

    /**
     * アンケートURL
     */
    @Column(name = "questionnaire_url", length = 1000)
    private String questionnaireUrl;

    /**
     * テストプレイで得られた気づき
     */
    @Column(columnDefinition = "TEXT")
    private String findings;

    /**
     * メモ
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected PlaytestRecord() {
    }

    /**
     * テストプレイ記録を生成します。
     *
     * @param id テストプレイ記録ID
     * @param projectId 作品ID
     * @param playedAt 実施日
     * @param platform 実施場所
     */
    public PlaytestRecord(final UUID id, final UUID projectId, final LocalDate playedAt,
                          final PlaytestPlatform platform) {
        super(id);
        this.projectId = projectId;
        this.playedAt = playedAt;
        this.platform = platform;
        this.questionnaireCollected = false;
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
     * 実施日を設定します。
     *
     * @param playedAt 実施日
     */
    public void setPlayedAt(final LocalDate playedAt) {
        this.playedAt = playedAt;
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
     * 実施場所を設定します。
     *
     * @param platform 実施場所
     */
    public void setPlatform(final PlaytestPlatform platform) {
        this.platform = platform;
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
     * 参加者数を設定します。
     *
     * @param participantCount 参加者数
     */
    public void setParticipantCount(final Integer participantCount) {
        this.participantCount = participantCount;
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
     * 実施したバージョンのメモを設定します。
     *
     * @param versionNote 実施したバージョンのメモ
     */
    public void setVersionNote(final String versionNote) {
        this.versionNote = versionNote;
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
     * アンケート回収状態を設定します。
     *
     * @param questionnaireCollected 回収済みの場合はtrue
     */
    public void setQuestionnaireCollected(final boolean questionnaireCollected) {
        this.questionnaireCollected = questionnaireCollected;
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
     * アンケートURLを設定します。
     *
     * @param questionnaireUrl アンケートURL
     */
    public void setQuestionnaireUrl(final String questionnaireUrl) {
        this.questionnaireUrl = questionnaireUrl;
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
     * テストプレイで得られた気づきを設定します。
     *
     * @param findings テストプレイで得られた気づき
     */
    public void setFindings(final String findings) {
        this.findings = findings;
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
     * メモを設定します。
     *
     * @param memo メモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
    }
}
