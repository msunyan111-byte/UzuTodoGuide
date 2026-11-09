package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.PlaytestRecord;
import com.example.uzu.domain.Project;
import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.GuideArticleRepository;
import com.example.uzu.repository.PlaytestRecordRepository;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.repository.ReviewIssueRepository;
import com.example.uzu.repository.ReviewRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 全データのバックアップと復元を行うサービスです。
 */
@Service
public class BackupService {

    /**
     * 作品リポジトリ
     */
    private final ProjectRepository projectRepository;

    /**
     * チェックリストリポジトリ
     */
    private final ChecklistItemRepository checklistItemRepository;

    /**
     * テストプレイ記録リポジトリ
     */
    private final PlaytestRecordRepository playtestRecordRepository;

    /**
     * 審査記録リポジトリ
     */
    private final ReviewRecordRepository reviewRecordRepository;

    /**
     * 審査指摘事項リポジトリ
     */
    private final ReviewIssueRepository reviewIssueRepository;

    /**
     * ガイド記事リポジトリ
     */
    private final GuideArticleRepository guideArticleRepository;

    /**
     * JSON変換用オブジェクトマッパー
     */
    private final ObjectMapper objectMapper;

    /**
     * バックアップサービスを生成します。
     *
     * @param projectRepository 作品リポジトリ
     * @param checklistItemRepository チェックリストリポジトリ
     * @param playtestRecordRepository テストプレイ記録リポジトリ
     * @param reviewRecordRepository 審査記録リポジトリ
     * @param reviewIssueRepository 審査指摘事項リポジトリ
     * @param guideArticleRepository ガイド記事リポジトリ
     * @param objectMapper JSON変換用オブジェクトマッパー
     */
    public BackupService(final ProjectRepository projectRepository,
                         final ChecklistItemRepository checklistItemRepository,
                         final PlaytestRecordRepository playtestRecordRepository,
                         final ReviewRecordRepository reviewRecordRepository,
                         final ReviewIssueRepository reviewIssueRepository,
                         final GuideArticleRepository guideArticleRepository,
                         final ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.playtestRecordRepository = playtestRecordRepository;
        this.reviewRecordRepository = reviewRecordRepository;
        this.reviewIssueRepository = reviewIssueRepository;
        this.guideArticleRepository = guideArticleRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 全データをエクスポートします。
     *
     * @return エクスポートしたデータ
     */
    public Map<String, Object> exportAll() {
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("projects", projectRepository.findAll());
        payload.put("checklistItems", checklistItemRepository.findAll());
        payload.put("playtestRecords", playtestRecordRepository.findAll());
        payload.put("reviewRecords", reviewRecordRepository.findAll());
        payload.put("reviewIssues", reviewIssueRepository.findAll());
        payload.put("guideArticles", guideArticleRepository.findAll());
        return payload;
    }

    /**
     * バックアップデータを復元します。
     *
     * @param payload 復元するデータ。nullまたは空の場合は現状をエクスポートして返す
     * @return 復元後の全データ
     */
    @Transactional
    public Map<String, Object> restore(final Map<String, Object> payload) {
        if (payload == null || payload.isEmpty()) {
            return exportAll();
        }

        restoreList(payload, "projects", Project.class, projectRepository::saveAll);
        restoreList(payload, "checklistItems", ChecklistItem.class, checklistItemRepository::saveAll);
        restoreList(payload, "playtestRecords", PlaytestRecord.class, playtestRecordRepository::saveAll);
        restoreList(payload, "reviewRecords", ReviewRecord.class, reviewRecordRepository::saveAll);
        restoreList(payload, "reviewIssues", ReviewIssue.class, reviewIssueRepository::saveAll);
        restoreList(payload, "guideArticles", GuideArticle.class, guideArticleRepository::saveAll);
        return exportAll();
    }

    /**
     * バックアップデータ内の一覧をエンティティへ変換して保存します。
     *
     * @param payload バックアップデータ
     * @param key 一覧のキー
     * @param type 変換先のエンティティ型
     * @param saver 保存処理
     * @param <T> エンティティ型
     */
    private <T> void restoreList(final Map<String, Object> payload, final String key, final Class<T> type,
                                 final Consumer<List<T>> saver) {
        final Object value = payload.get(key);
        if (!(value instanceof List)) {
            return;
        }
        final List<?> list = (List<?>) value;
        final List<T> entities = new ArrayList<>();
        for (final Object item : list) {
            if (item instanceof Map) {
                final Map<?, ?> map = (Map<?, ?>) item;
                entities.add(objectMapper.convertValue(map, type));
            }
        }
        if (!entities.isEmpty()) {
            saver.accept(entities);
        }
    }
}
