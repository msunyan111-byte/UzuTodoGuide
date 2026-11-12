package com.example.uzu.service;

import com.example.uzu.domain.GuideCategory;
import com.example.uzu.repository.ProjectRepository;
import com.example.uzu.web.error.ApiValidationException;
import com.example.uzu.web.error.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/**
 * {@link ServiceValidation} の必須入力・存在確認を検証する単体テストです。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceValidation の単体テスト")
class ServiceValidationTest {

    /** 作品リポジトリのモック。 */
    @Mock
    private ProjectRepository projectRepository;

    /**
     * 前後に空白がある文字列を渡したとき、前後の空白を除去した値を返すことを検証します。
     */
    @Test
    @DisplayName("必須文字列: 前後の空白を除去して返す")
    void requireText_trimsValue() {
        assertThat(ServiceValidation.requireText("  作品名  ", "title", "必須です。"))
                .isEqualTo("作品名");
    }

    /**
     * nullまたは空白だけの文字列を渡したとき、項目別の入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("必須文字列: nullと空白だけの値を拒否する")
    void requireText_rejectsNullOrBlank() {
        assertThatThrownBy(() -> ServiceValidation.requireText(null, "title", "必須です。"))
                .isInstanceOf(ApiValidationException.class);
        assertThatThrownBy(() -> ServiceValidation.requireText("   ", "title", "必須です。"))
                .isInstanceOf(ApiValidationException.class);
    }

    /**
     * 必須値が指定されている場合はその値を返し、nullの場合は入力エラーを返すことを検証します。
     */
    @Test
    @DisplayName("必須値: 値があれば返し、nullは拒否する")
    void requireValue_returnsValueAndRejectsNull() {
        assertThat(ServiceValidation.requireValue("UZU", "platform", "必須です。")).isEqualTo("UZU");
        assertThatThrownBy(() -> ServiceValidation.requireValue(null, "platform", "必須です。"))
                .isInstanceOf(ApiValidationException.class);
    }


    /**
     * 任意列挙値は未指定ならnullを返し、有効値は変換し、不正値は入力エラーになることを検証します。
     */
    @Test
    @DisplayName("任意列挙値: 未指定はnull、有効値は変換、不正値は拒否する")
    void requireOptionalEnum_handlesBlankValidAndInvalidValues() {
        assertThat(ServiceValidation.requireOptionalEnum(
                null, GuideCategory::fromValue, "category", "未対応のカテゴリです。")).isNull();
        assertThat(ServiceValidation.requireOptionalEnum(
                "   ", GuideCategory::fromValue, "category", "未対応のカテゴリです。")).isNull();
        assertThat(ServiceValidation.requireOptionalEnum(
                "作品概要", GuideCategory::fromValue, "category", "未対応のカテゴリです。"))
                .isEqualTo(GuideCategory.OVERVIEW);
        assertThatThrownBy(() -> ServiceValidation.requireOptionalEnum(
                "UNKNOWN", GuideCategory::fromValue, "category", "未対応のカテゴリです。"))
                .isInstanceOfSatisfying(ApiValidationException.class,
                        ex -> assertThat(ex.getDetails())
                                .extracting("field")
                                .containsExactly("category"));
    }

    /**
     * 作品が存在する場合は何もせず、存在しない場合はNotFoundを返すことを検証します。
     */
    @Test
    @DisplayName("作品存在確認: 存在すれば通過し、存在しなければNotFoundを返す")
    void requireProjectExists_checksRepository() {
        final UUID existing = UUID.randomUUID();
        final UUID missing = UUID.randomUUID();
        given(projectRepository.existsById(existing)).willReturn(true);
        given(projectRepository.existsById(missing)).willReturn(false);

        ServiceValidation.requireProjectExists(projectRepository, existing);

        assertThatThrownBy(() -> ServiceValidation.requireProjectExists(projectRepository, missing))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("作品が見つかりません。");
    }
}
