package com.example.uzu.service;

import com.example.uzu.domain.GuideFaq;
import com.example.uzu.domain.GuideFaqCategory;
import com.example.uzu.repository.GuideFaqRepository;
import com.example.uzu.web.dto.GuideFaqResponse;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * UZU機能ガイドの困ったときのQ&Aを提供するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class GuideFaqService {

    /**
     * Q&Aリポジトリ
     */
    private final GuideFaqRepository guideFaqRepository;

    /**
     * Q&Aサービスを生成します。
     *
     * @param guideFaqRepository Q&Aリポジトリ
     */
    public GuideFaqService(final GuideFaqRepository guideFaqRepository) {
        this.guideFaqRepository = guideFaqRepository;
    }

    /**
     * 条件に一致するQ&Aを取得します。
     *
     * @param categoryCode カテゴリコード。nullまたは空の場合は絞り込まない
     * @param keyword 検索キーワード。nullまたは空の場合は絞り込まない
     * @return Q&A一覧
     * @throws com.example.uzu.web.error.ApiValidationException 未対応のカテゴリが指定された場合
     */
    public List<GuideFaqResponse> list(final String categoryCode, final String keyword) {
        final GuideFaqCategory category = ServiceValidation.requireOptionalEnum(
                categoryCode, GuideFaqCategory::fromValue, "category", "未対応のカテゴリです。");
        final String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        return findFaqs(category).stream()
                .filter(faq -> matchesKeyword(faq, normalizedKeyword))
                .map(GuideFaqResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 指定されたQ&Aを取得します。
     *
     * @param faqId Q&AのID
     * @return Q&A
     * @throws NotFoundException Q&Aが見つからない場合
     */
    public GuideFaqResponse findById(final UUID faqId) {
        final GuideFaq faq = guideFaqRepository.findById(faqId)
                .filter(GuideFaq::isPublished)
                .orElseThrow(() -> new NotFoundException("Q&Aが見つかりません。"));
        return GuideFaqResponse.from(faq);
    }

    /**
     * カテゴリに応じてQ&Aを取得します。
     *
     * @param category カテゴリ。nullの場合は全件を取得する
     * @return Q&A一覧
     */
    private List<GuideFaq> findFaqs(final GuideFaqCategory category) {
        if (category != null) {
            return guideFaqRepository.findByPublishedTrueAndCategoryOrderByDisplayOrderAsc(category);
        }
        return guideFaqRepository.findByPublishedTrueOrderByDisplayOrderAsc();
    }

    /**
     * Q&Aが検索キーワードに一致するかどうかを判定します。
     *
     * @param faq Q&A
     * @param normalizedKeyword 正規化済みの検索キーワード
     * @return 一致する場合はtrue
     */
    private boolean matchesKeyword(final GuideFaq faq, final String normalizedKeyword) {
        if (normalizedKeyword.isEmpty()) {
            return true;
        }
        final String text = String.join(" ",
                faq.getQuestion(),
                faq.getAnswer(),
                faq.getCategory().getLabel(),
                faq.getSectionKey() == null ? "" : faq.getSectionKey())
                .toLowerCase(Locale.ROOT);
        return text.contains(normalizedKeyword);
    }
}
