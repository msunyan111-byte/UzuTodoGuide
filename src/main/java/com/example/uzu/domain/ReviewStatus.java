package com.example.uzu.domain;

/**
 * 審査ステータスを表す列挙型です。
 */
public enum ReviewStatus {
    DRAFT,
    SUBMITTED,
    REVISION_REQUESTED,
    QUALITY_INSUFFICIENT,
    PASSED
}
