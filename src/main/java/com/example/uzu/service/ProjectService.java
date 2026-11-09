package com.example.uzu.service;

import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.domain.Project;
import com.example.uzu.domain.Stage;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.ProjectCreateRequest;
import com.example.uzu.web.dto.ProjectResponse;
import com.example.uzu.web.dto.ProjectUpdateRequest;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 作品の取得・作成・更新・アーカイブを行うサービスです。
 */
@Service
@Transactional(readOnly = true)
public class ProjectService {

    /**
     * 更新日時の降順ソート
     */
    private static final Sort UPDATED_AT_DESC = Sort.by(Sort.Direction.DESC, "updatedAt");

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * チェックリストリポジトリ
     */
    private final ChecklistItemRepository checklistItemRepository;

    /**
     * 作品サービスを生成します。
     *
     * @param projectRepository 作品リポジトリ
     * @param checklistItemRepository チェックリストリポジトリ
     */
    public ProjectService(final ProjectRepository projectRepository,
                          final ChecklistItemRepository checklistItemRepository) {
        this.projectRepository = projectRepository;
        this.checklistItemRepository = checklistItemRepository;
    }

    /**
     * 作品一覧を取得します。
     *
     * @param status 絞り込む制作段階。nullの場合は絞り込まない
     * @param archived アーカイブ済み作品を対象とする場合はtrue。nullの場合はfalseとして扱う
     * @return 作品一覧
     */
    public List<ProjectResponse> findAll(final Stage status, final Boolean archived) {
        final boolean archivedValue = Boolean.TRUE.equals(archived);
        final List<Project> projects = status == null
                ? projectRepository.findAllByArchived(archivedValue, UPDATED_AT_DESC)
                : projectRepository.findAllByArchivedAndStage(archivedValue, status, UPDATED_AT_DESC);
        return projects.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /**
     * 指定された作品を取得します。
     *
     * @param projectId 作品ID
     * @return 作品
     * @throws NotFoundException 作品が見つからない場合
     */
    public ProjectResponse findById(final UUID projectId) {
        return toResponse(requireProject(projectId));
    }

    /**
     * 作品を作成し、公開形態に応じた標準チェックリストを登録します。
     *
     * @param request 作品作成リクエスト
     * @return 作成した作品
     * @throws com.example.uzu.web.error.ApiValidationException タイトルが空白のみの場合
     */
    @Transactional
    public ProjectResponse create(final ProjectCreateRequest request) {
        final String title = ServiceValidation.requireText(request.getTitle(), "title", "必須項目です。");
        Project project = new Project(UUID.randomUUID(), title, request.getReleaseType());
        if (request.getStage() != null) {
            project.setStage(request.getStage());
        }
        if (request.getScoringRule() != null) {
            project.setScoringRule(request.getScoringRule());
        }
        project.setFurigana(request.getFurigana());
        project.setGenre(request.getGenre());
        project.setPlayerCount(request.getPlayerCount());
        project.setEstimatedMinutes(request.getEstimatedMinutes());
        project.setStudioWorkId(request.getStudioWorkId());
        project.setStudioUrl(request.getStudioUrl());
        project.setPublicUrl(request.getPublicUrl());
        project.setMemo(request.getMemo());
        project = projectRepository.save(project);
        checklistItemRepository.saveAll(StandardChecklist.create(project.getId(), project.getReleaseType()));
        return toResponse(project);
    }

    /**
     * 指定された作品の情報を更新します。
     *
     * @param projectId 作品ID
     * @param request 作品更新リクエスト
     * @return 更新後の作品
     * @throws NotFoundException 作品が見つからない場合
     * @throws com.example.uzu.web.error.ApiValidationException タイトルが空白のみの場合
     */
    @Transactional
    public ProjectResponse update(final UUID projectId, final ProjectUpdateRequest request) {
        final Project project = requireProject(projectId);
        updateTitle(project, request.getTitle());
        if (request.getFurigana() != null) {
            project.setFurigana(request.getFurigana());
        }
        if (request.getGenre() != null) {
            project.setGenre(request.getGenre());
        }
        if (request.getPlayerCount() != null) {
            project.setPlayerCount(request.getPlayerCount());
        }
        if (request.getEstimatedMinutes() != null) {
            project.setEstimatedMinutes(request.getEstimatedMinutes());
        }
        if (request.getReleaseType() != null) {
            project.setReleaseType(request.getReleaseType());
        }
        if (request.getStage() != null) {
            project.setStage(request.getStage());
        }
        if (request.getScoringRule() != null) {
            project.setScoringRule(request.getScoringRule());
        }
        if (request.getStudioWorkId() != null) {
            project.setStudioWorkId(request.getStudioWorkId());
        }
        if (request.getStudioUrl() != null) {
            project.setStudioUrl(request.getStudioUrl());
        }
        if (request.getPublicUrl() != null) {
            project.setPublicUrl(request.getPublicUrl());
        }
        if (request.getMemo() != null) {
            project.setMemo(request.getMemo());
        }
        projectRepository.saveAndFlush(project);
        return toResponse(project);
    }

    /**
     * 指定された作品をアーカイブします。
     *
     * @param projectId 作品ID
     * @return アーカイブ後の作品
     * @throws NotFoundException 作品が見つからない場合
     */
    @Transactional
    public ProjectResponse archive(final UUID projectId) {
        final Project project = requireProject(projectId);
        project.setArchived(true);
        projectRepository.saveAndFlush(project);
        return toResponse(project);
    }

    /**
     * タイトルが指定されている場合のみ、前後の空白を除去して更新します。
     *
     * @param project 更新対象の作品
     * @param title 更新後のタイトル。nullの場合は更新しない
     * @throws com.example.uzu.web.error.ApiValidationException タイトルが空白のみの場合
     */
    private void updateTitle(final Project project, final String title) {
        if (title != null) {
            project.setTitle(ServiceValidation.requireText(title, "title", "必須項目です。"));
        }
    }

    /**
     * 指定された作品を取得し、存在しない場合は例外をスローします。
     *
     * @param projectId 作品ID
     * @return 作品
     * @throws NotFoundException 作品が見つからない場合
     */
    private Project requireProject(final UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("作品が見つかりません。"));
    }

    /**
     * 作品をレスポンスへ変換します。
     *
     * @param project 作品
     * @return 作品レスポンス
     */
    private ProjectResponse toResponse(final Project project) {
        final long total = checklistItemRepository.countByProjectId(project.getId());
        final long completed = checklistItemRepository.countByProjectIdAndStatus(
                project.getId(), ChecklistStatus.DONE);
        return ProjectResponse.from(project, completed, total);
    }
}
