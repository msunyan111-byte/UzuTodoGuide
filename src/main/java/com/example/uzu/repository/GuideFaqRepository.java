package com.example.uzu.repository;

import com.example.uzu.domain.GuideFaq;
import com.example.uzu.domain.GuideFaqCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * UZU機能ガイドのQ&Aを永続化するリポジトリです。
 */
public interface GuideFaqRepository extends JpaRepository<GuideFaq, UUID> {

    /**
     * 表示対象のQ&Aを表示順で取得します。
     *
     * @return 表示対象のQ&A一覧
     */
    List<GuideFaq> findByPublishedTrueOrderByDisplayOrderAsc();

    /**
     * 表示対象のQ&Aをカテゴリで絞り込んで表示順で取得します。
     *
     * @param category Q&Aカテゴリ
     * @return 表示対象のQ&A一覧
     */
    List<GuideFaq> findByPublishedTrueAndCategoryOrderByDisplayOrderAsc(GuideFaqCategory category);
}
