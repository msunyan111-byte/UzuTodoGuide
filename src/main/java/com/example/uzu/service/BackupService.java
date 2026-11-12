package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.GuideArticle;
import com.example.uzu.domain.GuideFaq;
import com.example.uzu.domain.PlaytestRecord;
import com.example.uzu.domain.Project;
import com.example.uzu.domain.ReviewIssue;
import com.example.uzu.domain.ReviewRecord;
import com.example.uzu.repository.ChecklistItemRepository;
import com.example.uzu.repository.GuideArticleRepository;
import com.example.uzu.repository.GuideFaqRepository;
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
     * バックアップ内の作品一覧キー
     */
    private static final String PROJECTS = "projects";

    /**
     * バックアップ内のチェックリスト一覧キー
     */
    private static final String CHECKLIST_ITEMS = "checklistItems";

    /**
     * バックアップ内のテストプレイ記録一覧キー
     */
    private static final String PLAYTEST_RECORDS = "playtestRecords";

    /**
     * バックアップ内の審査記録一覧キー
     */
    private static final String REVIEW_RECORDS = "reviewRecords";

    /**
     * バックアップ内の審査指摘事項一覧キー
     */
    private static final String REVIEW_ISSUES = "reviewIssues";

    /**
     * バックアップ内のガイド記事一覧キー
     */
    private static final String GUIDE_ARTICLES = "guideArticles";

    /**
     * バックアップ内のQ&A一覧キー
     */
    private static final String GUIDE_FAQS = "guideFaqs";

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
     * ガイドQ&Aリポジトリ
     */
    private final GuideFaqRepository guideFaqRepository;

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
     * @param guideFaqRepository ガイドQ&Aリポジトリ
     * @param objectMapper JSON変換用オブジェクトマッパー
     */
    public BackupService(final ProjectRepository projectRepository,
                         final ChecklistItemRepository checklistItemRepository,
                         final PlaytestRecordRepository playtestRecordRepository,
                         final ReviewRecordRepository reviewRecordRepository,
                         final ReviewIssueRepository reviewIssueRepository,
                         final GuideArticleRepository guideArticleRepository,
                         final GuideFaqRepository guideFaqRepository,
                         final ObjectMapper objectMapper) {
        this.projectRepository = projectRepository;
        this.checklistItemRepository = checklistItemRepository;
        this.playtestRecordRepository = playtestRecordRepository;
        this.reviewRecordRepository = reviewRecordRepository;
        this.reviewIssueRepository = reviewIssueRepository;
        this.guideArticleRepository = guideArticleRepository;
        this.guideFaqRepository = guideFaqRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * 全データをエクスポートします。
     *
     * @return エクスポートしたデータ
     */
    @Transactional(readOnly = true)
    public Map<String, Object> exportAll() {
        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put(PROJECTS, projectRepository.findAll());
        payload.put(CHECKLIST_ITEMS, checklistItemRepository.findAll());
        payload.put(PLAYTEST_RECORDS, playtestRecordRepository.findAll());
        payload.put(REVIEW_RECORDS, reviewRecordRepository.findAll());
        payload.put(REVIEW_ISSUES, reviewIssueRepository.findAll());
        payload.put(GUIDE_ARTICLES, guideArticleRepository.findAll());
        payload.put(GUIDE_FAQS, guideFaqRepository.findAll());
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

        restoreList(payload, PROJECTS, Project.class, projectRepository::saveAll);
        restoreList(payload, CHECKLIST_ITEMS, ChecklistItem.class, checklistItemRepository::saveAll);
        restoreList(payload, PLAYTEST_RECORDS, PlaytestRecord.class, playtestRecordRepository::saveAll);
        restoreList(payload, REVIEW_RECORDS, ReviewRecord.class, reviewRecordRepository::saveAll);
        restoreList(payload, REVIEW_ISSUES, ReviewIssue.class, reviewIssueRepository::saveAll);
        restoreList(payload, GUIDE_ARTICLES, GuideArticle.class, guideArticleRepository::saveAll);
        restoreList(payload, GUIDE_FAQS, GuideFaq.class, guideFaqRepository::saveAll);
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
