package com.example.uzu.repository;

import com.example.uzu.domain.Project;
import com.example.uzu.domain.Stage;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 作品を永続化するリポジトリです。
 */
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * アーカイブ状態で作品を取得します。
     *
     * @param archived アーカイブ状態
     * @param sort ソート条件
     * @return 作品一覧
     */
    List<Project> findAllByArchived(boolean archived, Sort sort);

    /**
     * アーカイブ状態と制作段階で作品を取得します。
     *
     * @param archived アーカイブ状態
     * @param stage 制作段階
     * @param sort ソート条件
     * @return 作品一覧
     */
    List<Project> findAllByArchivedAndStage(boolean archived, Stage stage, Sort sort);
}
