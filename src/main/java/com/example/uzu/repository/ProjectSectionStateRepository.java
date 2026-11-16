package com.example.uzu.repository;

import com.example.uzu.domain.ProjectSectionState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * 作品の画面セクション状態を永続化するリポジトリです。
 */
public interface ProjectSectionStateRepository extends JpaRepository<ProjectSectionState, UUID> {

    /**
     * 作品IDとセクションキーに一致するセクション状態を取得します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @return セクション状態
     */
    Optional<ProjectSectionState> findByProjectIdAndSectionKey(UUID projectId, String sectionKey);
}
