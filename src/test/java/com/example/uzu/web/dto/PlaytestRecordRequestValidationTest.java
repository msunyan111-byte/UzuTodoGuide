package com.example.uzu.web.dto;

import com.example.uzu.domain.PlaytestPlatform;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * テストプレイ記録リクエストのBean Validationを検証する単体テストです。
 */
@DisplayName("テストプレイ記録リクエストの検証")
class PlaytestRecordRequestValidationTest {

    /** 入力検証器。 */
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 作成リクエストで参加者数・バージョンメモ・アンケートURLが不正な場合、違反になることを検証します。
     */
    @Test
    @DisplayName("作成: 参加者数と文字列長の不正を検出する")
    void createRequest_rejectsInvalidValues() {
        PlaytestRecordCreateRequest request = new PlaytestRecordCreateRequest();
        request.setPlayedAt(LocalDate.of(2026, 11, 12));
        request.setPlatform(PlaytestPlatform.UZU);
        request.setParticipantCount(0);
        request.setVersionNote("x".repeat(201));
        request.setQuestionnaireUrl("x".repeat(1001));

        Set<ConstraintViolation<PlaytestRecordCreateRequest>> violations = validator.validate(request);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("participantCount", "versionNote", "questionnaireUrl");
    }

    /**
     * 更新リクエストで参加者数・バージョンメモ・アンケートURLが不正な場合、違反になることを検証します。
     */
    @Test
    @DisplayName("更新: 参加者数と文字列長の不正を検出する")
    void updateRequest_rejectsInvalidValues() {
        PlaytestRecordUpdateRequest request = new PlaytestRecordUpdateRequest();
        request.setParticipantCount(-1);
        request.setVersionNote("x".repeat(201));
        request.setQuestionnaireUrl("x".repeat(1001));

        Set<ConstraintViolation<PlaytestRecordUpdateRequest>> violations = validator.validate(request);

        assertThat(violations).extracting(violation -> violation.getPropertyPath().toString())
                .containsExactlyInAnyOrder("participantCount", "versionNote", "questionnaireUrl");
    }
}
