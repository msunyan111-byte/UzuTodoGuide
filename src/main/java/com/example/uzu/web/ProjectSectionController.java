package com.example.uzu.web;

import com.example.uzu.service.ProjectSectionService;
import com.example.uzu.web.dto.ProjectSectionStateResponse;
import com.example.uzu.web.dto.ProjectSectionStateUpdateRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

/**
 * 作品の画面セクション状態APIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/sections")
public class ProjectSectionController {

    /**
     * セクション状態サービス
     */
    private final ProjectSectionService projectSectionService;

    /**
     * セクション状態コントローラを生成します。
     *
     * @param projectSectionService セクション状態サービス
     */
    public ProjectSectionController(final ProjectSectionService projectSectionService) {
        this.projectSectionService = projectSectionService;
    }

    /**
     * 指定された作品とセクションの状態を取得します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @return セクション状態
     */
    @GetMapping("/{sectionKey}")
    public ProjectSectionStateResponse get(@PathVariable final UUID projectId,
                                           @PathVariable final String sectionKey) {
        return projectSectionService.find(projectId, sectionKey);
    }

    /**
     * 指定された作品とセクションの状態を保存します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @param request セクション状態更新リクエスト
     * @return 保存後のセクション状態
     */
    @PatchMapping("/{sectionKey}")
    public ProjectSectionStateResponse save(@PathVariable final UUID projectId,
                                            @PathVariable final String sectionKey,
                                            @Valid @RequestBody final ProjectSectionStateUpdateRequest request) {
        return projectSectionService.save(projectId, sectionKey, request);
    }
}
