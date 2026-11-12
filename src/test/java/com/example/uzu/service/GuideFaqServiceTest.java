package com.example.uzu.service;

import com.example.uzu.domain.GuideFaq;
import com.example.uzu.domain.GuideFaqCategory;
import com.example.uzu.repository.GuideFaqRepository;
import com.example.uzu.web.dto.GuideFaqResponse;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * {@link GuideFaqService} のカテゴリ絞り込みと公開判定を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GuideFaqService の単体テスト")
class GuideFaqServiceTest {

    /** Q&Aリポジトリのモック。 */
    @Mock
    private GuideFaqRepository guideFaqRepository;

    /** テスト対象のサービス。 */
    @InjectMocks
    private GuideFaqService guideFaqService;

    /**
     * カテゴリが空白の場合、未指定として全公開Q&Aを取得することを検証します。
     */
    @Test
    @DisplayName("Q&A検索: 空白カテゴリは未指定として全件を返す")
    void list_treatsBlankCategoryAsUnspecified() {
        GuideFaq faq = faq("質問", GuideFaqCategory.OTHER, true);
        given(guideFaqRepository.findByPublishedTrueOrderByDisplayOrderAsc()).willReturn(List.of(faq));

        List<GuideFaqResponse> result = guideFaqService.list("  ", null);

        assertThat(result).extracting(GuideFaqResponse::getQuestion).containsExactly("質問");
    }

    /**
     * カテゴリを指定した場合、そのカテゴリの公開Q&Aを取得することを検証します。
     */
    @Test
    @DisplayName("Q&A検索: カテゴリ指定時は該当カテゴリを返す")
    void list_filtersByCategory() {
        GuideFaq faq = faq("質問", GuideFaqCategory.CHECK, true);
        given(guideFaqRepository.findByPublishedTrueAndCategoryOrderByDisplayOrderAsc(
                GuideFaqCategory.CHECK)).willReturn(List.of(faq));

        List<GuideFaqResponse> result = guideFaqService.list("CHECK", "確認");

        assertThat(result).extracting(GuideFaqResponse::getCategory)
                .containsExactly("CHECK");
    }

    /**
     * 未対応のカテゴリを指定した場合、入力エラーになることを検証します。
     */
    @Test
    @DisplayName("Q&A検索: 未対応カテゴリは入力エラーにする")
    void list_rejectsUnknownCategory() {
        assertThatThrownBy(() -> guideFaqService.list("UNKNOWN", null))
                .isInstanceOf(ApiValidationException.class);
    }


    /**
     * セクションキーが未設定のQ&Aでもキーワード検索できることを検証します。
     */
    @Test
    @DisplayName("Q&A検索: セクションキー未設定でも検索できる")
    void list_matchesKeywordWhenSectionKeyIsNull() {
        GuideFaq faq = new GuideFaq(UUID.randomUUID(), "質問", "回答", GuideFaqCategory.OTHER,
                null, "https://docs.studio.uzu-app.com/", LocalDate.of(2026, 11, 12), 1, true);
        given(guideFaqRepository.findByPublishedTrueOrderByDisplayOrderAsc()).willReturn(List.of(faq));

        List<GuideFaqResponse> result = guideFaqService.list(null, "質問");

        assertThat(result).hasSize(1);
    }

    /**
     * 公開Q&Aを取得できることを検証します。
     */
    @Test
    @DisplayName("Q&A取得: 公開中のQ&Aを返す")
    void findById_returnsPublishedFaq() {
        UUID id = UUID.randomUUID();
        given(guideFaqRepository.findById(id))
                .willReturn(Optional.of(faq("質問", GuideFaqCategory.OTHER, true)));

        GuideFaqResponse response = guideFaqService.findById(id);

        assertThat(response.getQuestion()).isEqualTo("質問");
    }

    /**
     * 非公開Q&Aを取得しようとした場合、NotFoundを返すことを検証します。
     */
    @Test
    @DisplayName("Q&A取得: 非公開Q&AはNotFoundを返す")
    void findById_throwsWhenFaqIsUnpublished() {
        UUID id = UUID.randomUUID();
        given(guideFaqRepository.findById(id))
                .willReturn(Optional.of(faq("質問", GuideFaqCategory.OTHER, false)));

        assertThatThrownBy(() -> guideFaqService.findById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Q&Aが見つかりません。");
    }

    /**
     * テスト用のQ&Aを生成します。
     *
     * @param question 質問文
     * @param category カテゴリ
     * @param published 公開対象かどうか
     * @return Q&A
     */
    private GuideFaq faq(final String question, final GuideFaqCategory category, final boolean published) {
        return new GuideFaq(UUID.randomUUID(), question, "回答", category, "SUMMARY",
                "https://docs.studio.uzu-app.com/", LocalDate.of(2026, 11, 12), 1, published);
    }
}
