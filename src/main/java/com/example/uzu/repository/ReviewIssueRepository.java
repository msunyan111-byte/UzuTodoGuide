package com.example.uzu.repository;

import com.example.uzu.domain.ReviewIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 審査指摘事項を永続化するリポジトリです。
 */
public interface ReviewIssueRepository extends JpaRepository<ReviewIssue, UUID> {

    /**
     * 審査記録IDに一致する審査指摘事項を作成日時の昇順で取得します。
     *
     * @param reviewId 審査記録ID
     * @return 審査指摘事項一覧
     */
    List<ReviewIssue> findByReviewIdOrderByCreatedAtAsc(UUID reviewId);
}
