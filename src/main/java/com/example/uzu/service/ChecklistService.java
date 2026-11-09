package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.ChecklistItemCreateRequest;
import com.example.uzu.web.dto.ChecklistItemResponse;
import com.example.uzu.web.dto.ChecklistItemUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 作品のチェックリストを管理するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class ChecklistService {

    /**
     * チェックリストリポジトリ
     */
    private final ChecklistItemRepository checklistItemRepository;

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * チェックリストサービスを生成します。
     *
     * @param checklistItemRepository チェックリストリポジトリ
     * @param projectRepository 作品リポジトリ
     */
    public ChecklistService(final ChecklistItemRepository checklistItemRepository,
                            final ProjectRepository projectRepository) {
        this.checklistItemRepository = checklistItemRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * 指定された作品のチェックリストを表示順で取得します。
     *
     * @param projectId 作品ID
     * @return チェックリスト
     * @throws NotFoundException 作品が見つからない場合
     */
    public List<ChecklistItemResponse> findAll(final UUID projectId) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        return checklistItemRepository.findByProjectIdOrderByDisplayOrderAsc(projectId).stream()
                .map(ChecklistItemResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * チェックリスト項目を追加します。
     *
     * @param projectId 作品ID
     * @param request チェックリスト項目作成リクエスト
     * @return 追加したチェックリスト項目
     * @throws NotFoundException 作品が見つからない場合
     * @throws ApiValidationException 項目名が空白のみの場合
     */
    @Transactional
    public ChecklistItemResponse add(final UUID projectId, final ChecklistItemCreateRequest request) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        final String name = ServiceValidation.requireText(request.getName(), "name", "必須項目です。");
        final int displayOrder = request.getDisplayOrder() != null
                ? request.getDisplayOrder()
                : (int) checklistItemRepository.countByProjectId(projectId) + 1;
        final ChecklistItem item = new ChecklistItem(UUID.randomUUID(), projectId, name, displayOrder);
        item.setDescription(request.getDescription());
        item.setMemo(request.getMemo());
        item.setDueDate(request.getDueDate());
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        return ChecklistItemResponse.from(checklistItemRepository.saveAndFlush(item));
    }

    /**
     * チェックリスト項目を更新します。
     *
     * @param projectId 作品ID
     * @param itemId チェックリスト項目ID
     * @param request チェックリスト項目更新リクエスト
     * @return 更新後のチェックリスト項目
     * @throws NotFoundException チェックリスト項目が見つからない場合
     * @throws ApiValidationException 項目名が空白のみの場合
     */
    @Transactional
    public ChecklistItemResponse update(final UUID projectId, final UUID itemId,
                                        final ChecklistItemUpdateRequest request) {
        final ChecklistItem item = requireItem(projectId, itemId);
        if (request.getName() != null) {
            item.setName(ServiceValidation.requireText(request.getName(), "name", "必須項目です。"));
        }
        if (request.getDescription() != null) {
            item.setDescription(request.getDescription());
        }
        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        if (request.getMemo() != null) {
            item.setMemo(request.getMemo());
        }
        if (request.getDueDate() != null) {
            item.setDueDate(request.getDueDate());
        }
        if (request.getDisplayOrder() != null) {
            item.setDisplayOrder(request.getDisplayOrder());
        }
        return ChecklistItemResponse.from(checklistItemRepository.saveAndFlush(item));
    }

    /**
     * チェックリスト項目を削除します。
     *
     * @param projectId 作品ID
     * @param itemId チェックリスト項目ID
     * @throws NotFoundException チェックリスト項目が見つからない場合
     */
    @Transactional
    public void delete(final UUID projectId, final UUID itemId) {
        final ChecklistItem item = requireItem(projectId, itemId);
        checklistItemRepository.delete(item);
    }

    /**
     * 指定されたチェックリスト項目を取得し、存在しない場合は例外をスローします。
     *
     * @param projectId 作品ID
     * @param itemId チェックリスト項目ID
     * @return チェックリスト項目
     * @throws NotFoundException チェックリスト項目が見つからない場合
     */
    private ChecklistItem requireItem(final UUID projectId, final UUID itemId) {
        return checklistItemRepository.findByIdAndProjectId(itemId, projectId)
                .orElseThrow(() -> new NotFoundException("チェック項目が見つかりません。"));
    }
}
