package com.example.uzu.domain;

/**
 * 作品の制作段階を表す列挙型です。
 */
public enum Stage {
    PLANNING,
    IN_PROGRESS,
    FUNCTIONAL_CHECK,
    PLAYTEST,
    REVISION,
    REVIEW_SUBMITTED,
    REVIEW_RESPONSE,
    RELEASE_PREPARATION,
    RELEASED
}
