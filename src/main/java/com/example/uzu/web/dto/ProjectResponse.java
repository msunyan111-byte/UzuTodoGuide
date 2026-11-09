package com.example.uzu.web.dto;

import com.example.uzu.domain.Project;
import com.example.uzu.domain.ReleaseType;
import com.example.uzu.domain.ScoringRule;
import com.example.uzu.domain.Stage;
import com.example.uzu.support.TokyoTime;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 作品レスポンスを表すDTOです。
 */
public class ProjectResponse {

    /**
     * 作品ID
     */
    private final UUID id;

    /**
     * 作品タイトル
     */
    private final String title;

    /**
     * ふりがな
     */
    private final String furigana;

    /**
     * ジャンル
     */
    private final String genre;

    /**
     * プレイ人数
     */
    private final Integer playerCount;

    /**
     * 想定プレイ時間（分）
     */
    private final Integer estimatedMinutes;

    /**
     * 公開形態
     */
    private final ReleaseType releaseType;

    /**
     * 制作段階
     */
    private final Stage stage;

    /**
     * 得点ルール
     */
    private final ScoringRule scoringRule;

    /**
     * UZU Studioの作品ID
     */
    private final String studioWorkId;

    /**
     * UZU Studioの作品URL
     */
    private final String studioUrl;

    /**
     * 公開URL
     */
    private final String publicUrl;

    /**
     * 作品メモ
     */
    private final String memo;

    /**
     * アーカイブ済みの場合はtrue
     */
    private final boolean archived;

    /**
     * 完了済みチェックリスト項目数
     */
    private final long checklistCompleted;

    /**
     * チェックリスト項目総数
     */
    private final long checklistTotal;

    /**
     * 作成日時
     */
    private final OffsetDateTime createdAt;

    /**
     * 更新日時
     */
    private final OffsetDateTime updatedAt;

    /**
     * 作品レスポンスを生成します。
     *
     * @param id 作品ID
     * @param title 作品タイトル
     * @param furigana ふりがな
     * @param genre ジャンル
     * @param playerCount プレイ人数
     * @param estimatedMinutes 想定プレイ時間（分）
     * @param releaseType 公開形態
     * @param stage 制作段階
     * @param scoringRule 得点ルール
     * @param studioWorkId UZU Studioの作品ID
     * @param studioUrl UZU Studioの作品URL
     * @param publicUrl 公開URL
     * @param memo 作品メモ
     * @param archived アーカイブ済みの場合はtrue
     * @param checklistCompleted 完了済みチェックリスト項目数
     * @param checklistTotal チェックリスト項目総数
     * @param createdAt 作成日時
     * @param updatedAt 更新日時
     */
    public ProjectResponse(final UUID id, final String title, final String furigana, final String genre,
                           final Integer playerCount, final Integer estimatedMinutes,
                           final ReleaseType releaseType, final Stage stage, final ScoringRule scoringRule,
                           final String studioWorkId, final String studioUrl, final String publicUrl,
                           final String memo, final boolean archived, final long checklistCompleted,
                           final long checklistTotal, final OffsetDateTime createdAt,
                           final OffsetDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.furigana = furigana;
        this.genre = genre;
        this.playerCount = playerCount;
        this.estimatedMinutes = estimatedMinutes;
        this.releaseType = releaseType;
        this.stage = stage;
        this.scoringRule = scoringRule;
        this.studioWorkId = studioWorkId;
        this.studioUrl = studioUrl;
        this.publicUrl = publicUrl;
        this.memo = memo;
        this.archived = archived;
        this.checklistCompleted = checklistCompleted;
        this.checklistTotal = checklistTotal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * 作品エンティティからレスポンスを生成します。
     *
     * @param project 作品エンティティ
     * @param checklistCompleted 完了済みチェックリスト項目数
     * @param checklistTotal チェックリスト項目総数
     * @return 作品レスポンス
     */
    public static ProjectResponse from(final Project project, final long checklistCompleted,
                                       final long checklistTotal) {
        return new ProjectResponse(
                project.getId(),
                project.getTitle(),
                project.getFurigana(),
                project.getGenre(),
                project.getPlayerCount(),
                project.getEstimatedMinutes(),
                project.getReleaseType(),
                project.getStage(),
                project.getScoringRule(),
                project.getStudioWorkId(),
                project.getStudioUrl(),
                project.getPublicUrl(),
                project.getMemo(),
                project.isArchived(),
                checklistCompleted,
                checklistTotal,
                TokyoTime.toOffset(project.getCreatedAt()),
                TokyoTime.toOffset(project.getUpdatedAt()));
    }

    /**
     * 作品IDを取得します。
     *
     * @return 作品ID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 作品タイトルを取得します。
     *
     * @return 作品タイトル
     */
    public String getTitle() {
        return title;
    }

    /**
     * ふりがなを取得します。
     *
     * @return ふりがな
     */
    public String getFurigana() {
        return furigana;
    }

    /**
     * ジャンルを取得します。
     *
     * @return ジャンル
     */
    public String getGenre() {
        return genre;
    }

    /**
     * プレイ人数を取得します。
     *
     * @return プレイ人数
     */
    public Integer getPlayerCount() {
        return playerCount;
    }

    /**
     * 想定プレイ時間を取得します。
     *
     * @return 想定プレイ時間（分）
     */
    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    /**
     * 公開形態を取得します。
     *
     * @return 公開形態
     */
    public ReleaseType getReleaseType() {
        return releaseType;
    }

    /**
     * 制作段階を取得します。
     *
     * @return 制作段階
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * 得点ルールを取得します。
     *
     * @return 得点ルール
     */
    public ScoringRule getScoringRule() {
        return scoringRule;
    }

    /**
     * UZU Studioの作品IDを取得します。
     *
     * @return UZU Studioの作品ID
     */
    public String getStudioWorkId() {
        return studioWorkId;
    }

    /**
     * UZU Studioの作品URLを取得します。
     *
     * @return UZU Studioの作品URL
     */
    public String getStudioUrl() {
        return studioUrl;
    }

    /**
     * 公開URLを取得します。
     *
     * @return 公開URL
     */
    public String getPublicUrl() {
        return publicUrl;
    }

    /**
     * 作品メモを取得します。
     *
     * @return 作品メモ
     */
    public String getMemo() {
        return memo;
    }

    /**
     * アーカイブ済みかどうかを取得します。
     *
     * @return アーカイブ済みの場合はtrue
     */
    public boolean isArchived() {
        return archived;
    }

    /**
     * 完了済みチェックリスト項目数を取得します。
     *
     * @return 完了済みチェックリスト項目数
     */
    public long getChecklistCompleted() {
        return checklistCompleted;
    }

    /**
     * チェックリスト項目総数を取得します。
     *
     * @return チェックリスト項目総数
     */
    public long getChecklistTotal() {
        return checklistTotal;
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
