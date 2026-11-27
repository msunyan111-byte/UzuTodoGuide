package com.example.uzu.repository;

import com.example.uzu.domain.PlaytestPlatform;
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

    /**
     * 作品IDに一致するテストプレイ記録数を取得します。
     *
     * @param projectId 作品ID
     * @return テストプレイ記録数
     */
    long countByProjectId(UUID projectId);

    /**
     * 作品IDと実施場所に一致するテストプレイ記録数を取得します。
     *
     * @param projectId 作品ID
     * @param platform 実施場所
     * @return テストプレイ記録数
     */
    long countByProjectIdAndPlatform(UUID projectId, PlaytestPlatform platform);

    /**
     * 作品IDに一致し、アンケートを回収済みのテストプレイ記録数を取得します。
     *
     * @param projectId 作品ID
     * @return アンケート回収済みのテストプレイ記録数
     */
    long countByProjectIdAndQuestionnaireCollectedTrue(UUID projectId);
}
