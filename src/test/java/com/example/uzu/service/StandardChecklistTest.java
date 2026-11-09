package com.example.uzu.service;

import com.example.uzu.domain.ChecklistItem;
import com.example.uzu.domain.ChecklistStatus;
import com.example.uzu.domain.ReleaseType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link StandardChecklist} の件数・画面名・説明文の品質を検証する単体テストです。
 */
@DisplayName("標準チェックリストの内容テスト")
class StandardChecklistTest {

    /**
     * UZU画面定義ファイル
     */
    private static final Path UZU_SECTIONS = Path.of("frontend/src/data/uzu.ts");

    /**
     * サンプルシナリオのTODOを再構成するマイグレーション
     */
    private static final Path SAMPLE_MIGRATION =
            Path.of("src/main/resources/db/migration/V6__restructure_vampire_mansion_todos.sql");

    /**
     * 画面名プレフィックスを抽出する正規表現
     */
    private static final Pattern SCREEN_PREFIX = Pattern.compile("^【([^】]+)】");

    /**
     * SQL内のTODO名を抽出する正規表現
     */
    private static final Pattern SQL_ITEM_NAME = Pattern.compile("'(【[^】]+】[^']+)'");

    /**
     * SQL内の完了条件を含む説明を抽出する正規表現
     */
    private static final Pattern SQL_COMPLETION = Pattern.compile("'[^']*完了条件:[^']*'");

    /**
     * 無料作品では有料公開専用項目を除いた34件が作成されることを検証します。
     */
    @Test
    @DisplayName("無料作品: 標準チェックリストを34件作成する")
    void create_freeProject_createsThirtyFourItems() {
        final List<ChecklistItem> items = StandardChecklist.create(UUID.randomUUID(), ReleaseType.FREE);

        assertThat(items).hasSize(34);
        assertThat(items).extracting(ChecklistItem::getName)
                .doesNotContain("【規約同意】売上受取人全員の同意を確認する")
                .doesNotContain("【売上・振込】振込先を登録する");
    }

    /**
     * 有料作品では無料作品の項目に有料公開専用の2件を加えた36件が作成されることを検証します。
     */
    @Test
    @DisplayName("有料作品: 有料公開専用2件を含む36件を作成する")
    void create_paidProject_addsTwoPaidItems() {
        final List<ChecklistItem> freeItems = StandardChecklist.create(UUID.randomUUID(), ReleaseType.FREE);
        final List<ChecklistItem> paidItems = StandardChecklist.create(UUID.randomUUID(), ReleaseType.PAID);

        assertThat(paidItems).hasSize(36);
        assertThat(paidItems).extracting(ChecklistItem::getName)
                .containsAll(freeItems.stream().map(ChecklistItem::getName).collect(Collectors.toList()))
                .contains("【規約同意】売上受取人全員の同意を確認する")
                .contains("【売上・振込】振込先を登録する");
    }

    /**
     * すべての項目が画面名プレフィックスと完了条件を持ち、表示順が連番であることを検証します。
     */
    @Test
    @DisplayName("全項目: 画面名・完了条件・表示順・初期状態がそろっている")
    void create_allItems_haveScreenPrefixCompletionAndSequentialOrder() {
        final List<ChecklistItem> items = StandardChecklist.create(UUID.randomUUID(), ReleaseType.PAID);

        assertThat(items).extracting(ChecklistItem::getName).doesNotHaveDuplicates();
        assertThat(items).allSatisfy(item -> {
            assertThat(item.getName()).matches("^【[^】]+】.+");
            assertThat(item.getDescription()).contains("完了条件:");
            assertThat(item.getStatus()).isEqualTo(ChecklistStatus.NOT_STARTED);
        });
        assertThat(items).extracting(ChecklistItem::getDisplayOrder)
                .containsExactlyElementsOf(
                        IntStream.rangeClosed(1, items.size()).boxed().collect(Collectors.toList()));
    }

    /**
     * 標準チェックリストの画面名がフロントエンドのUZU画面定義に存在することを検証します。
     *
     * @throws IOException UZU画面定義を読み込めない場合
     */
    @Test
    @DisplayName("全項目: 画面名がフロントエンドのUZU画面定義と一致する")
    void create_allPrefixes_existInFrontendSections() throws IOException {
        final Set<String> labels = readUzuLabels();
        final List<ChecklistItem> items = StandardChecklist.create(UUID.randomUUID(), ReleaseType.PAID);

        assertThat(labels).isNotEmpty();
        assertThat(items).allSatisfy(item -> assertThat(labels).contains(prefixOf(item.getName())));
    }

    /**
     * サンプルシナリオのマイグレーションが46件の画面名付きTODOと完了条件を持つことを検証します。
     *
     * @throws IOException マイグレーションを読み込めない場合
     */
    @Test
    @DisplayName("サンプルシナリオ: 46件の画面名付きTODOと完了条件を持つ")
    void sampleMigration_hasFortySixGuidedItems() throws IOException {
        final String sql = Files.readString(SAMPLE_MIGRATION, StandardCharsets.UTF_8);
        final List<String> names = extractSqlItemNames(sql);
        final int completionCount = countMatches(SQL_COMPLETION, sql);

        assertThat(names).hasSize(46).doesNotHaveDuplicates();
        assertThat(completionCount).isEqualTo(46);
    }

    /**
     * サンプルシナリオの画面名もフロントエンドのUZU画面定義に存在することを検証します。
     *
     * @throws IOException 対象ファイルを読み込めない場合
     */
    @Test
    @DisplayName("サンプルシナリオ: 画面名がフロントエンドのUZU画面定義と一致する")
    void sampleMigration_allPrefixes_existInFrontendSections() throws IOException {
        final Set<String> labels = readUzuLabels();
        final String sql = Files.readString(SAMPLE_MIGRATION, StandardCharsets.UTF_8);

        assertThat(extractSqlItemNames(sql)).allSatisfy(name -> assertThat(labels).contains(prefixOf(name)));
    }

    /**
     * フロントエンドのUZU画面ラベルを読み取ります。
     *
     * @return 画面ラベルの集合
     * @throws IOException UZU画面定義を読み込めない場合
     */
    private Set<String> readUzuLabels() throws IOException {
        final String source = Files.readString(UZU_SECTIONS, StandardCharsets.UTF_8);
        final Matcher matcher = Pattern.compile("label:\\s*\"([^\"]+)\"").matcher(source);
        final Set<String> labels = new LinkedHashSet<>();
        while (matcher.find()) {
            labels.add(matcher.group(1));
        }
        return labels;
    }

    /**
     * SQLからTODO名を抽出します。
     *
     * @param sql マイグレーションSQL
     * @return TODO名の一覧
     */
    private List<String> extractSqlItemNames(final String sql) {
        final Matcher matcher = SQL_ITEM_NAME.matcher(sql);
        final List<String> names = new ArrayList<>();
        while (matcher.find()) {
            names.add(matcher.group(1));
        }
        return names;
    }

    /**
     * 項目名から画面名プレフィックスを取り出します。
     *
     * @param name 項目名
     * @return 画面名
     */
    private String prefixOf(final String name) {
        final Matcher matcher = SCREEN_PREFIX.matcher(name);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    /**
     * 正規表現に一致する件数を数えます。
     *
     * @param pattern 検索する正規表現
     * @param text 検索対象
     * @return 一致件数
     */
    private int countMatches(final Pattern pattern, final String text) {
        final Matcher matcher = pattern.matcher(text);
        int count = 0;
        while (matcher.find()) {
            count++;
        }
        return count;
    }
}
