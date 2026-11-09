package com.example.uzu.repository;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.ChecklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * チェックリスト項目を永続化するリポジトリです。
 */
public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, UUID> {

    /**
     * 作品IDに一致するチェックリスト項目を表示順で取得します。
     *
     * @param projectId 作品ID
     * @return チェックリスト項目一覧
     */
    List<ChecklistItem> findByProjectIdOrderByDisplayOrderAsc(UUID projectId);

    /**
     * 項目IDと作品IDに一致するチェックリスト項目を取得します。
     *
     * @param id 項目ID
     * @param projectId 作品ID
     * @return チェックリスト項目
     */
    Optional<ChecklistItem> findByIdAndProjectId(UUID id, UUID projectId);

    /**
     * 作品IDに一致するチェックリスト項目数を取得します。
     *
     * @param projectId 作品ID
     * @return チェックリスト項目数
     */
    long countByProjectId(UUID projectId);

    /**
     * 作品IDと状態に一致するチェックリスト項目数を取得します。
     *
     * @param projectId 作品ID
     * @param status 状態
     * @return チェックリスト項目数
     */
    long countByProjectIdAndStatus(UUID projectId, ChecklistStatus status);
}
