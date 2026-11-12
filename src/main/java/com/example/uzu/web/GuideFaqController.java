package com.example.uzu.web;

import com.example.uzu.service.GuideFaqService;
import com.example.uzu.web.dto.GuideFaqResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * UZU機能ガイドの困ったときのQ&A APIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1")
public class GuideFaqController {

    /**
     * Q&Aサービス
     */
    private final GuideFaqService guideFaqService;

    /**
     * Q&Aコントローラを生成します。
     *
     * @param guideFaqService Q&Aサービス
     */
    public GuideFaqController(final GuideFaqService guideFaqService) {
        this.guideFaqService = guideFaqService;
    }

    /**
     * 条件に一致するQ&Aを取得します。
     *
     * @param category カテゴリコード
     * @param keyword 検索キーワード
     * @return Q&A一覧
     */
    @GetMapping("/guide-faqs")
    public List<GuideFaqResponse> list(@RequestParam(required = false) final String category,
                                       @RequestParam(required = false) final String keyword) {
        return guideFaqService.list(category, keyword);
    }

    /**
     * 指定されたQ&Aを取得します。
     *
     * @param faqId Q&AのID
     * @return Q&A
     */
    @GetMapping("/guide-faqs/{faqId}")
    public GuideFaqResponse get(@PathVariable final UUID faqId) {
        return guideFaqService.findById(faqId);
    }
}
