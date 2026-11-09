package com.example.uzu.service;

import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.GuideCategory;
import com.example.uzu.repository.GuideArticleRepository;
import com.example.uzu.web.dto.GuideArticleResponse;
import com.example.uzu.web.dto.GuideCategoryResponse;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.ErrorDetail;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UZU公式ガイドの初期データと参照機能を提供するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class GuideService {

    /**
     * 更新日時の降順ソート
     */
    private static final Sort UPDATED_AT_DESC = Sort.by(Sort.Direction.DESC, "updatedAt");

    /**
     * ガイド記事リポジトリ
     */
    private final GuideArticleRepository guideArticleRepository;

    /**
     * ガイドサービスを生成します。
     *
     * @param guideArticleRepository ガイド記事リポジトリ
     */
    public GuideService(final GuideArticleRepository guideArticleRepository) {
        this.guideArticleRepository = guideArticleRepository;
    }

    /**
     * ガイド記事が未登録の場合に初期記事を登録します。
     */
    @PostConstruct
    public void seedIfEmpty() {
        if (guideArticleRepository.count() > 0) {
            return;
        }
        final LocalDate checkedAt = LocalDate.now();
        final List<GuideArticle> seedArticles = List.of(
                new GuideArticle(UUID.randomUUID(), "作成からリリースまでの流れ",
                        GuideCategory.MAKING_FLOW,
                        "作成から公開までの基本的な流れと確認ポイントを要約したガイドです。",
                        "企画立案、設計、動作確認、テストプレイ、修正、審査、公開準備までの一連の工程を順に確認します。",
                        "https://docs.studio.uzu-app.com/overview/makingflow.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "UZU Studio v2でできること・できないこと",
                        GuideCategory.OVERVIEW,
                        "作品制作者が事前に把握しておくべき制約と機能の整理です。",
                        "UZU Studio v2 でできることとできないことを整理し、制作計画や審査準備の前提を確認します。",
                        "https://docs.studio.uzu-app.com/overview/available.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "ホーム全体について",
                        GuideCategory.OVERVIEW,
                        "作品一覧と公開の基本操作を把握するための入口ガイドです。",
                        "ホーム画面の見方やメニューの位置関係、公開準備の確認ポイントを理解します。",
                        "https://docs.studio.uzu-app.com/scenariohome/main-screen.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "テストプレイ",
                        GuideCategory.PLAYTEST,
                        "テストプレイでの観点と改善の運用方法を整理したガイドです。",
                        "実施日、回数、参加者数、改善点の記録とフィードバックの扱いを整理します。",
                        "https://docs.studio.uzu-app.com/overview/makingflow/testplay.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "リリース管理",
                        GuideCategory.REVIEW,
                        "公開準備とセルフチェックの際に確認するポイントを整理したガイドです。",
                        "公開予定日、料金、セール、告知情報を確認し、リリース管理に必要な情報を準備します。",
                        "https://docs.studio.uzu-app.com/scenariohome/release.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "詳細審査（有料公開作品）",
                        GuideCategory.REVIEW,
                        "有料公開作品の審査要件と提出前の確認事項です。",
                        "有料公開作品では評価の基準や提出前の確認が重要になるため、検討すべき観点を整理します。",
                        "https://docs.studio.uzu-app.com/overview/makingflow/submit/check.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "エディタ機能の確認ポイント",
                        GuideCategory.EDITOR,
                        "エディタでの操作や設定項目の見落としを防ぐためのメモです。",
                        "条件分岐、キャラクター、フェーズ、配布物の設定が実際の作品動作と整合しているかを確認します。",
                        "https://docs.studio.uzu-app.com/overview/available.md",
                        checkedAt),
                new GuideArticle(UUID.randomUUID(), "FAQ",
                        GuideCategory.FAQ,
                        "制作時によくある疑問と確認観点の要点をまとめたガイドです。",
                        "公開前のセルフチェック、修正後の再確認、審査対応の進め方など、よくある疑問に対して確認事項をまとめます。",
                        "https://docs.studio.uzu-app.com/llms.txt",
                        checkedAt)
        );
        guideArticleRepository.saveAll(seedArticles);
    }

    /**
     * ガイドカテゴリ一覧を取得します。
     *
     * @return ガイドカテゴリ一覧
     */
    public List<GuideCategoryResponse> categories() {
        return Arrays.stream(GuideCategory.values())
                .map(GuideCategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 条件に一致するガイド記事を取得します。
     *
     * @param categoryCode カテゴリコード。nullまたは空の場合は絞り込まない
     * @param keyword 検索キーワード。nullまたは空の場合は絞り込まない
     * @return ガイド記事一覧
     * @throws ApiValidationException 未対応のカテゴリが指定された場合
     */
    public List<GuideArticleResponse> list(final String categoryCode, final String keyword) {
        final GuideCategory category = GuideCategory.fromValue(categoryCode);
        if (categoryCode != null && category == null) {
            throw new ApiValidationException(List.of(new ErrorDetail("category", "未対応のカテゴリです。")));
        }
        return findArticles(category, keyword).stream()
                .map(GuideArticleResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 指定されたガイド記事を取得します。
     *
     * @param guideId ガイド記事ID
     * @return ガイド記事
     * @throws NotFoundException ガイド記事が見つからない場合
     */
    public GuideArticleResponse findById(final UUID guideId) {
        final GuideArticle article = guideArticleRepository.findById(guideId)
                .orElseThrow(() -> new NotFoundException("ガイドが見つかりません。"));
        return GuideArticleResponse.from(article);
    }

    /**
     * キーワードとカテゴリに応じてガイド記事を検索します。
     *
     * @param category カテゴリ。nullの場合はカテゴリで絞り込まない
     * @param keyword 検索キーワード。nullまたは空の場合はキーワードで絞り込まない
     * @return ガイド記事一覧
     */
    private List<GuideArticle> findArticles(final GuideCategory category, final String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            final String normalized = keyword.trim();
            return guideArticleRepository
                    .findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrContentContainingIgnoreCase(
                            normalized, normalized, normalized);
        }
        if (category != null) {
            return guideArticleRepository.findByCategoryOrderByUpdatedAtDesc(category);
        }
        return guideArticleRepository.findAll(UPDATED_AT_DESC);
    }
}
