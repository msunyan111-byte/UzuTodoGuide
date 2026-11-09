package com.example.uzu.web;

import com.example.uzu.service.GuideService;
import com.example.uzu.web.dto.GuideArticleResponse;
import com.example.uzu.web.dto.GuideCategoryResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * ガイド記事APIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1")
public class GuideController {

    /**
     * ガイドサービス
     */
    private final GuideService guideService;

    /**
     * ガイドコントローラを生成します。
     *
     * @param guideService ガイドサービス
     */
    public GuideController(final GuideService guideService) {
        this.guideService = guideService;
    }

    /**
     * 条件に一致するガイド記事を取得します。
     *
     * @param category カテゴリコード
     * @param keyword 検索キーワード
     * @return ガイド記事一覧
     */
    @GetMapping("/guides")
    public List<GuideArticleResponse> list(@RequestParam(required = false) final String category,
                                           @RequestParam(required = false) final String keyword) {
        return guideService.list(category, keyword);
    }

    /**
     * 指定されたガイド記事を取得します。
     *
     * @param guideId ガイド記事ID
     * @return ガイド記事
     */
    @GetMapping("/guides/{guideId}")
    public GuideArticleResponse get(@PathVariable final UUID guideId) {
        return guideService.findById(guideId);
    }

    /**
     * ガイドカテゴリ一覧を取得します。
     *
     * @return ガイドカテゴリ一覧
     */
    @GetMapping("/guide-categories")
    public List<GuideCategoryResponse> categories() {
        return guideService.categories();
    }
}
