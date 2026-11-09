package com.example.uzu.repository;

import com.example.uzu.domain.ReviewRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 審査記録を永続化するリポジトリです。
 */
public interface ReviewRecordRepository extends JpaRepository<ReviewRecord, UUID> {

    /**
     * 作品IDに一致する審査記録を取得します。
     *
     * @param projectId 作品ID
     * @param sort ソート条件
     * @return 審査記録一覧
     */
    List<ReviewRecord> findByProjectId(UUID projectId, Sort sort);
}
