package com.example.uzu.service;

import com.example.uzu.domain.ProjectSectionState;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ProjectSectionStateRepository;
import com.example.uzu.support.TokyoTime;
import com.example.uzu.web.dto.ProjectSectionStateResponse;
import com.example.uzu.web.dto.ProjectSectionStateUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 作品の画面セクションごとのメモと最終確認日時を管理するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class ProjectSectionService {

    /**
     * セクションキーの形式
     */
    private static final Pattern SECTION_KEY_PATTERN = Pattern.compile("[A-Z0-9_]+");

    /**
     * セクションキーの最大文字数
     */
    private static final int SECTION_KEY_MAX_LENGTH = 50;

    /**
     * セクション状態リポジトリ
     */
    private final ProjectSectionStateRepository projectSectionStateRepository;

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * セクション状態サービスを生成します。
     *
     * @param projectSectionStateRepository セクション状態リポジトリ
     * @param projectRepository 作品リポジトリ
     */
    public ProjectSectionService(final ProjectSectionStateRepository projectSectionStateRepository,
                                 final ProjectRepository projectRepository) {
        this.projectSectionStateRepository = projectSectionStateRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * 指定された作品とセクションの状態を取得します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @return セクション状態
     * @throws NotFoundException 作品またはセクション状態が見つからない場合
     * @throws ApiValidationException セクションキーの形式が不正な場合
     */
    public ProjectSectionStateResponse find(final UUID projectId, final String sectionKey) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        final String validSectionKey = requireSectionKey(sectionKey);
        return projectSectionStateRepository.findByProjectIdAndSectionKey(projectId, validSectionKey)
                .map(ProjectSectionStateResponse::from)
                .orElseThrow(() -> new NotFoundException("セクションメモが見つかりません。"));
    }

    /**
     * 指定された作品とセクションの状態を保存します。
     *
     * @param projectId 作品ID
     * @param sectionKey 画面セクションのキー
     * @param request セクション状態更新リクエスト
     * @return 保存後のセクション状態
     * @throws NotFoundException 作品が見つからない場合
     * @throws ApiValidationException セクションキーの形式が不正な場合
     */
    @Transactional
    public ProjectSectionStateResponse save(final UUID projectId, final String sectionKey,
                                            final ProjectSectionStateUpdateRequest request) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        final String validSectionKey = requireSectionKey(sectionKey);
        final ProjectSectionState state = projectSectionStateRepository
                .findByProjectIdAndSectionKey(projectId, validSectionKey)
                .orElseGet(() -> new ProjectSectionState(UUID.randomUUID(), projectId, validSectionKey));
        state.setMemo(request.getMemo());
        state.setVerifiedAt(toTokyoLocalDateTime(request.getVerifiedAt()));
        return ProjectSectionStateResponse.from(projectSectionStateRepository.saveAndFlush(state));
    }

    /**
     * セクションキーを検証して返します。
     *
     * @param sectionKey 画面セクションのキー
     * @return 検証済みのセクションキー
     * @throws ApiValidationException セクションキーが空白または形式不正な場合
     */
    private String requireSectionKey(final String sectionKey) {
        final String value = ServiceValidation.requireText(
                sectionKey, "sectionKey", "セクションキーは必須です。");
        if (value.length() > SECTION_KEY_MAX_LENGTH || !SECTION_KEY_PATTERN.matcher(value).matches()) {
            throw ApiValidationException.of("sectionKey", "セクションキーの形式が不正です。");
        }
        return value;
    }

    /**
     * オフセット付き日時を東京時間のローカル日時に変換します。
     *
     * @param value 変換対象の日時。nullの場合はnullを返す
     * @return 東京時間のローカル日時
     */
    private LocalDateTime toTokyoLocalDateTime(final OffsetDateTime value) {
        return value == null ? null : value.atZoneSameInstant(TokyoTime.ZONE).toLocalDateTime();
    }
}
