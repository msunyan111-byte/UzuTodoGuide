package com.example.uzu.service;

import com.example.uzu.domain.PlaytestRecord;
import com.example.uzu.repository.PlaytestRecordRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.dto.PlaytestRecordCreateRequest;
import com.example.uzu.web.dto.PlaytestRecordResponse;
import com.example.uzu.web.dto.PlaytestRecordUpdateRequest;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 作品のテストプレイ記録を管理するサービスです。
 */
@Service
@Transactional(readOnly = true)
public class PlaytestService {

    /**
     * 実施日の降順ソート
     */
    private static final Sort PLAYED_AT_DESC = Sort.by(Sort.Direction.DESC, "playedAt");

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * テストプレイ記録リポジトリ
     */
    private final PlaytestRecordRepository playtestRecordRepository;

    /**
     * テストプレイサービスを生成します。
     *
     * @param projectRepository 作品リポジトリ
     * @param playtestRecordRepository テストプレイ記録リポジトリ
     */
    public PlaytestService(final ProjectRepository projectRepository,
                           final PlaytestRecordRepository playtestRecordRepository) {
        this.projectRepository = projectRepository;
        this.playtestRecordRepository = playtestRecordRepository;
    }

    /**
     * 指定された作品のテストプレイ記録を取得します。
     *
     * @param projectId 作品ID
     * @return テストプレイ記録
     * @throws NotFoundException 作品が見つからない場合
     */
    public List<PlaytestRecordResponse> findAll(final UUID projectId) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        return playtestRecordRepository.findByProjectId(projectId, PLAYED_AT_DESC).stream()
                .map(PlaytestRecordResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * テストプレイ記録を作成します。
     *
     * @param projectId 作品ID
     * @param request テストプレイ記録作成リクエスト
     * @return 作成したテストプレイ記録
     * @throws NotFoundException 作品が見つからない場合
     * @throws ApiValidationException 実施日または実施場所が未指定の場合
     */
    @Transactional
    public PlaytestRecordResponse create(final UUID projectId, final PlaytestRecordCreateRequest request) {
        ServiceValidation.requireProjectExists(projectRepository, projectId);
        final PlaytestRecord record = new PlaytestRecord(
                UUID.randomUUID(),
                projectId,
                ServiceValidation.requireValue(request.getPlayedAt(), "playedAt", "実施日は必須です。"),
                ServiceValidation.requireValue(request.getPlatform(), "platform", "実施場所は必須です。"));
        if (request.getParticipantCount() != null) {
            record.setParticipantCount(request.getParticipantCount());
        }
        if (request.getVersionNote() != null) {
            record.setVersionNote(request.getVersionNote());
        }
        record.setQuestionnaireCollected(Boolean.TRUE.equals(request.getQuestionnaireCollected()));
        record.setQuestionnaireUrl(request.getQuestionnaireUrl());
        record.setFindings(request.getFindings());
        record.setMemo(request.getMemo());
        return PlaytestRecordResponse.from(playtestRecordRepository.saveAndFlush(record));
    }

    /**
     * テストプレイ記録を更新します。
     *
     * @param projectId 作品ID
     * @param playtestId テストプレイ記録ID
     * @param request テストプレイ記録更新リクエスト
     * @return 更新後のテストプレイ記録
     * @throws NotFoundException テストプレイ記録が見つからない場合
     */
    @Transactional
    public PlaytestRecordResponse update(final UUID projectId, final UUID playtestId,
                                         final PlaytestRecordUpdateRequest request) {
        final PlaytestRecord record = requirePlaytest(projectId, playtestId);
        if (request.getPlayedAt() != null) {
            record.setPlayedAt(request.getPlayedAt());
        }
        if (request.getPlatform() != null) {
            record.setPlatform(request.getPlatform());
        }
        if (request.getParticipantCount() != null) {
            record.setParticipantCount(request.getParticipantCount());
        }
        if (request.getVersionNote() != null) {
            record.setVersionNote(request.getVersionNote());
        }
        if (request.getQuestionnaireCollected() != null) {
            record.setQuestionnaireCollected(request.getQuestionnaireCollected());
        }
        if (request.getQuestionnaireUrl() != null) {
            record.setQuestionnaireUrl(request.getQuestionnaireUrl());
        }
        if (request.getFindings() != null) {
            record.setFindings(request.getFindings());
        }
        if (request.getMemo() != null) {
            record.setMemo(request.getMemo());
        }
        return PlaytestRecordResponse.from(playtestRecordRepository.saveAndFlush(record));
    }

    /**
     * テストプレイ記録を削除します。
     *
     * @param projectId 作品ID
     * @param playtestId テストプレイ記録ID
     * @throws NotFoundException テストプレイ記録が見つからない場合
     */
    @Transactional
    public void delete(final UUID projectId, final UUID playtestId) {
        final PlaytestRecord record = requirePlaytest(projectId, playtestId);
        playtestRecordRepository.delete(record);
    }

    /**
     * 指定されたテストプレイ記録を取得し、存在しない場合は例外をスローします。
     *
     * @param projectId 作品ID
     * @param playtestId テストプレイ記録ID
     * @return テストプレイ記録
     * @throws NotFoundException テストプレイ記録が見つからない場合
     */
    private PlaytestRecord requirePlaytest(final UUID projectId, final UUID playtestId) {
        return playtestRecordRepository.findById(playtestId)
                .filter(record -> record.getProjectId().equals(projectId))
                .orElseThrow(() -> new NotFoundException("テストプレイ記録が見つかりません。"));
    }
}
