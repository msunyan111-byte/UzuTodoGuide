package com.example.uzu.web;

import com.example.uzu.domain.Stage;
import com.example.uzu.service.ProjectService;
import com.example.uzu.web.dto.ProjectCreateRequest;
import com.example.uzu.web.dto.ProjectResponse;
import com.example.uzu.web.dto.ProjectUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * 作品APIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    /**
     * 作品サービス
     */
    private final ProjectService projectService;

    /**
     * 作品コントローラを生成します。
     *
     * @param projectService 作品サービス
     */
    public ProjectController(final ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * 作品一覧を取得します。
     *
     * @param status 絞り込む制作段階
     * @param archived アーカイブ済み作品を対象とする場合はtrue
     * @return 作品一覧
     */
    @GetMapping
    public List<ProjectResponse> list(@RequestParam(required = false) final Stage status,
                                      @RequestParam(required = false) final Boolean archived) {
        return projectService.findAll(status, archived);
    }

    /**
     * 作品を作成します。
     *
     * @param request 作品作成リクエスト
     * @return 作成した作品
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody final ProjectCreateRequest request) {
        return projectService.create(request);
    }

    /**
     * 指定された作品を取得します。
     *
     * @param projectId 作品ID
     * @return 作品
     */
    @GetMapping("/{projectId}")
    public ProjectResponse get(@PathVariable final UUID projectId) {
        return projectService.findById(projectId);
    }

    /**
     * 指定された作品を更新します。
     *
     * @param projectId 作品ID
     * @param request 作品更新リクエスト
     * @return 更新後の作品
     */
    @PatchMapping("/{projectId}")
    public ProjectResponse update(@PathVariable final UUID projectId,
                                  @Valid @RequestBody final ProjectUpdateRequest request) {
        return projectService.update(projectId, request);
    }

    /**
     * 指定された作品をアーカイブします。
     *
     * @param projectId 作品ID
     * @return アーカイブ後の作品
     */
    @PostMapping("/{projectId}/archive")
    public ProjectResponse archive(@PathVariable final UUID projectId) {
        return projectService.archive(projectId);
    }
}
