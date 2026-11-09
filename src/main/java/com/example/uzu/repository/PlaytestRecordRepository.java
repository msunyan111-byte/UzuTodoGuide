package com.example.uzu.repository;

import com.example.uzu.domain.PlaytestRecord;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * テストプレイ記録を永続化するリポジトリです。
 */
public interface PlaytestRecordRepository extends JpaRepository<PlaytestRecord, UUID> {

    /**
     * 作品IDに一致するテストプレイ記録を取得します。
     *
     * @param projectId 作品ID
     * @param sort ソート条件
     * @return テストプレイ記録一覧
     */
    List<PlaytestRecord> findByProjectId(UUID projectId, Sort sort);
}
