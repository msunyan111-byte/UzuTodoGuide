package com.example.uzu.service;

import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.GuideCategory;
import com.example.uzu.repository.GuideArticleRepository;
import com.example.uzu.support.TokyoTime;
import com.example.uzu.web.dto.GuideArticleResponse;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * {@link GuideService} の初期データと検索条件を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GuideService の単体テスト")
class GuideServiceTest {

    /** ガイド記事リポジトリのモック。 */
    @Mock
    private GuideArticleRepository guideArticleRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private GuideService guideService;

    /**
     * 既存のガイド記事がある場合、初期データを再登録しないことを検証します。
     */
    @Test
    @DisplayName("初期データ: 既存記事がある場合は登録しない")
    void seedIfEmpty_doesNothingWhenArticlesExist() {
        given(guideArticleRepository.count()).willReturn(1L);

        guideService.seedIfEmpty();

        verify(guideArticleRepository, never()).saveAll(any());
    }

    /**
     * ガイド記事が空の場合、確認日を東京時間の今日として8件登録することを検証します。
     */
    @Test
    @DisplayName("初期データ: 記事が空の場合は8件登録する")
    void seedIfEmpty_createsInitialArticles() {
        given(guideArticleRepository.count()).willReturn(0L);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<GuideArticle>> captor = ArgumentCaptor.forClass(List.class);
        given(guideArticleRepository.saveAll(captor.capture()))
                .willAnswer(invocation -> invocation.getArgument(0));

        guideService.seedIfEmpty();

        assertThat(captor.getValue()).hasSize(8);
        assertThat(captor.getValue()).extracting(GuideArticle::getCheckedAt)
                .containsOnly(TokyoTime.today());
    }

    /**
     * カテゴリとキーワードが空白の場合、全記事を取得することを検証します。
     */
    @Test
    @DisplayName("ガイド検索: 空白の条件では全記事を返す")
    void list_returnsAllWhenConditionsAreBlank() {
        GuideArticle article = article(GuideCategory.OVERVIEW, "概要");
        given(guideArticleRepository.findAll(any(Sort.class))).willReturn(List.of(article));

        List<GuideArticleResponse> result = guideService.list("  ", "  ");

        assertThat(result).extracting(GuideArticleResponse::getTitle).containsExactly("概要");
    }

    /**
     * カテゴリを指定した場合、そのカテゴリの記事だけを取得することを検証します。
     */
    @Test
    @DisplayName("ガイド検索: カテゴリ指定時は該当カテゴリを返す")
    void list_filtersByCategory() {
        GuideArticle article = article(GuideCategory.REVIEW, "審査");
        given(guideArticleRepository.findByCategoryOrderByUpdatedAtDesc(GuideCategory.REVIEW))
                .willReturn(List.of(article));

        List<GuideArticleResponse> result = guideService.list("REVIEW", null);

        assertThat(result).extracting(GuideArticleResponse::getCategory)
                .containsExactly(GuideCategory.REVIEW);
    }

    /**
     * 未対応のカテゴリを指定した場合、入力エラーになることを検証します。
     */
    @Test
    @DisplayName("ガイド検索: 未対応カテゴリは入力エラーにする")
    void list_rejectsUnknownCategory() {
        assertThatThrownBy(() -> guideService.list("UNKNOWN", null))
                .isInstanceOf(ApiValidationException.class);
    }

    /**
     * 存在しないガイドIDを指定した場合、NotFoundを返すことを検証します。
     */
    @Test
    @DisplayName("ガイド取得: 存在しないIDではNotFoundを返す")
    void findById_throwsWhenMissing() {
        UUID id = UUID.randomUUID();
        given(guideArticleRepository.findById(id)).willReturn(Optional.empty());

        assertThatThrownBy(() -> guideService.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("ガイドが見つかりません。");
    }

    /**
     * テスト用のガイド記事を生成します。
     *
     * @param category カテゴリ
     * @param title タイトル
     * @return ガイド記事
     */
    private GuideArticle article(final GuideCategory category, final String title) {
        return new GuideArticle(UUID.randomUUID(), title, category, "概要", "本文",
                "https://docs.studio.uzu-app.com/", LocalDate.of(2026, 11, 12));
    }
}
