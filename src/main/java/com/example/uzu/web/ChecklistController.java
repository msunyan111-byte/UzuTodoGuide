package com.example.uzu.web;

import com.example.uzu.service.ChecklistService;
import com.example.uzu.web.dto.ChecklistItemCreateRequest;
import com.example.uzu.web.dto.ChecklistItemResponse;
import com.example.uzu.web.dto.ChecklistItemUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * チェックリストAPIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/checklist")
public class ChecklistController {

    /**
     * チェックリストサービス
     */
    private final ChecklistService checklistService;

    /**
     * チェックリストコントローラを生成します。
     *
     * @param checklistService チェックリストサービス
     */
    public ChecklistController(final ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    /**
     * 指定された作品のチェックリストを取得します。
     *
     * @param projectId 作品ID
     * @return チェックリスト
     */
    @GetMapping
    public List<ChecklistItemResponse> list(@PathVariable final UUID projectId) {
        return checklistService.findAll(projectId);
    }

    /**
     * チェックリスト項目を追加します。
     *
     * @param projectId 作品ID
     * @param request チェックリスト項目作成リクエスト
     * @return 追加したチェックリスト項目
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistItemResponse add(@PathVariable final UUID projectId,
                                     @Valid @RequestBody final ChecklistItemCreateRequest request) {
        return checklistService.add(projectId, request);
    }

    /**
     * チェックリスト項目を更新します。
     *
     * @param projectId 作品ID
     * @param itemId チェックリスト項目ID
     * @param request チェックリスト項目更新リクエスト
     * @return 更新後のチェックリスト項目
     */
    @PatchMapping("/{itemId}")
    public ChecklistItemResponse update(@PathVariable final UUID projectId,
                                        @PathVariable final UUID itemId,
                                        @Valid @RequestBody final ChecklistItemUpdateRequest request) {
        return checklistService.update(projectId, itemId, request);
    }

    /**
     * チェックリスト項目を削除します。
     *
     * @param projectId 作品ID
     * @param itemId チェックリスト項目ID
     */
    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID projectId, @PathVariable final UUID itemId) {
        checklistService.delete(projectId, itemId);
    }
}
