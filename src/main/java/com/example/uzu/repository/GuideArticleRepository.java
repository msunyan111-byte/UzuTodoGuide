package com.example.uzu.repository;

import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.GuideCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * ガイド記事を永続化するリポジトリです。
 */
public interface GuideArticleRepository extends JpaRepository<GuideArticle, UUID> {

    /**
     * カテゴリに一致するガイド記事を更新日時の降順で取得します。
     *
     * @param category カテゴリ
     * @return ガイド記事一覧
     */
    List<GuideArticle> findByCategoryOrderByUpdatedAtDesc(GuideCategory category);

    /**
     * タイトル・概要・本文のいずれかにキーワードを含むガイド記事を取得します。
     *
     * @param titleKeyword タイトル検索キーワード
     * @param summaryKeyword 概要検索キーワード
     * @param contentKeyword 本文検索キーワード
     * @return ガイド記事一覧
     */
    List<GuideArticle> findByTitleContainingIgnoreCaseOrSummaryContainingIgnoreCaseOrContentContainingIgnoreCase(
            String titleKeyword, String summaryKeyword, String contentKeyword);
}
