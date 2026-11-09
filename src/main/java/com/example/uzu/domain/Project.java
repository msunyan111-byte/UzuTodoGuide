package com.example.uzu.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import java.util.UUID;

/**
 * 制作中の作品を表すエンティティです。
 */
@Entity
@Table(name = "projects")
public class Project extends AuditedEntity {

    /**
     * 作品タイトル
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * 作品タイトルのふりがな
     */
    @Column(length = 200)
    private String furigana;

    /**
     * ジャンル
     */
    @Column(length = 100)
    private String genre;

    /**
     * プレイ人数
     */
    @Column(name = "player_count")
    private Integer playerCount;

    /**
     * 想定プレイ時間（分）
     */
    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    /**
     * 公開形態
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "release_type", nullable = false, length = 20)
    private ReleaseType releaseType;

    /**
     * 制作段階
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Stage stage;

    /**
     * 得点ルール
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "scoring_rule", nullable = false, length = 30)
    private ScoringRule scoringRule;

    /**
     * UZU Studioの作品ID
     */
    @Column(name = "studio_work_id", length = 200)
    private String studioWorkId;

    /**
     * UZU Studioの作品URL
     */
    @Column(name = "studio_url", length = 1000)
    private String studioUrl;

    /**
     * 公開URL
     */
    @Column(name = "public_url", length = 1000)
    private String publicUrl;

    /**
     * 作品メモ
     */
    @Column(columnDefinition = "TEXT")
    private String memo;

    /**
     * アーカイブ済みの場合はtrue
     */
    @Column(nullable = false)
    private boolean archived;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected Project() {
    }

    /**
     * 作品を生成します。
     *
     * @param id 作品ID
     * @param title 作品タイトル
     * @param releaseType 公開形態
     */
    public Project(final UUID id, final String title, final ReleaseType releaseType) {
        super(id);
        this.title = title;
        this.releaseType = releaseType;
        this.stage = Stage.PLANNING;
        this.scoringRule = ScoringRule.TIE_ALLOWED;
        this.archived = false;
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
     * 作品タイトルを設定します。
     *
     * @param title 作品タイトル
     */
    public void setTitle(final String title) {
        this.title = title;
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
     * ふりがなを設定します。
     *
     * @param furigana ふりがな
     */
    public void setFurigana(final String furigana) {
        this.furigana = furigana;
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
     * ジャンルを設定します。
     *
     * @param genre ジャンル
     */
    public void setGenre(final String genre) {
        this.genre = genre;
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
     * プレイ人数を設定します。
     *
     * @param playerCount プレイ人数
     */
    public void setPlayerCount(final Integer playerCount) {
        this.playerCount = playerCount;
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
     * 想定プレイ時間を設定します。
     *
     * @param estimatedMinutes 想定プレイ時間（分）
     */
    public void setEstimatedMinutes(final Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
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
     * 公開形態を設定します。
     *
     * @param releaseType 公開形態
     */
    public void setReleaseType(final ReleaseType releaseType) {
        this.releaseType = releaseType;
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
     * 制作段階を設定します。
     *
     * @param stage 制作段階
     */
    public void setStage(final Stage stage) {
        this.stage = stage;
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
     * 得点ルールを設定します。
     *
     * @param scoringRule 得点ルール
     */
    public void setScoringRule(final ScoringRule scoringRule) {
        this.scoringRule = scoringRule;
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
     * UZU Studioの作品IDを設定します。
     *
     * @param studioWorkId UZU Studioの作品ID
     */
    public void setStudioWorkId(final String studioWorkId) {
        this.studioWorkId = studioWorkId;
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
     * UZU Studioの作品URLを設定します。
     *
     * @param studioUrl UZU Studioの作品URL
     */
    public void setStudioUrl(final String studioUrl) {
        this.studioUrl = studioUrl;
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
     * 公開URLを設定します。
     *
     * @param publicUrl 公開URL
     */
    public void setPublicUrl(final String publicUrl) {
        this.publicUrl = publicUrl;
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
     * 作品メモを設定します。
     *
     * @param memo 作品メモ
     */
    public void setMemo(final String memo) {
        this.memo = memo;
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
     * アーカイブ状態を設定します。
     *
     * @param archived アーカイブ済みの場合はtrue
     */
    public void setArchived(final boolean archived) {
        this.archived = archived;
    }
}
