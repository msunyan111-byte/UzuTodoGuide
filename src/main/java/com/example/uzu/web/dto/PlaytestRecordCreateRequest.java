package com.example.uzu.web.dto;

import com.example.uzu.domain.PlaytestPlatform;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * テストプレイ記録作成リクエストを表すDTOです。
 */
public class PlaytestRecordCreateRequest {

    /**
     * 実施バージョンメモの最大文字数
     */
    private static final int VERSION_NOTE_MAX_LENGTH = 200;

    /**
     * アンケートURLの最大文字数
     */
    private static final int QUESTIONNAIRE_URL_MAX_LENGTH = 1000;

    /**
     * 実施日
     */
    @NotNull
    private LocalDate playedAt;

    /**
     * 実施場所
     */
    @NotNull
    private PlaytestPlatform platform;

    /**
     * 参加者数
     */
    @Positive
    private Integer participantCount;

    /**
     * 実施したバージョンのメモ
     */
    @Size(max = VERSION_NOTE_MAX_LENGTH)
    private String versionNote;

    /**
     * アンケートを回収済みの場合はtrue
     */
    private Boolean questionnaireCollected;

    /**
     * アンケートURL
     */
    @Size(max = QUESTIONNAIRE_URL_MAX_LENGTH)
    private String questionnaireUrl;

    /**
     * テストプレイで得られた気づき
     */
    private String findings;

    /**
     * メモ
     */
    private String memo;

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
    public Boolean getQuestionnaireCollected() {
        return questionnaireCollected;
    }

    /**
     * アンケート回収状態を設定します。
     *
     * @param questionnaireCollected 回収済みの場合はtrue
     */
    public void setQuestionnaireCollected(final Boolean questionnaireCollected) {
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
