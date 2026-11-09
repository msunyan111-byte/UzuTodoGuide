package com.example.uzu.support;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

/**
 * 東京時間の取得と変換を提供するユーティリティクラスです。
 */
public final class TokyoTime {

    /**
     * 東京のタイムゾーン
     */
    public static final ZoneId ZONE = ZoneId.of("Asia/Tokyo");

    private TokyoTime() {
    }

    /**
     * 東京時間の現在日時を取得します。
     *
     * @return 東京時間の現在日時
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZONE);
    }

    /**
     * 東京時間の日時をオフセット付き日時に変換します。
     *
     * @param value 変換対象の日時。nullの場合はnullを返す
     * @return オフセット付き日時
     */
    public static OffsetDateTime toOffset(final LocalDateTime value) {
        return value == null ? null : value.atZone(ZONE).toOffsetDateTime();
    }
}
