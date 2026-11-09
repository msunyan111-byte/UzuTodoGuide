package com.example.uzu.domain;

import com.example.uzu.support.TokyoTime;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 識別子と作成日時・更新日時を共通で持つエンティティの基底クラスです。
 */
@MappedSuperclass
public abstract class AuditedEntity {

    /**
     * エンティティID
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * 作成日時
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新日時
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * JPA用のデフォルトコンストラクタです。
     */
    protected AuditedEntity() {
    }

    /**
     * エンティティIDを指定して生成します。
     *
     * @param id エンティティID
     */
    protected AuditedEntity(final UUID id) {
        this.id = id;
    }

    /**
     * 作成時に作成日時と更新日時を設定します。
     */
    @PrePersist
    void onCreate() {
        final LocalDateTime now = TokyoTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * 更新時に更新日時を設定します。
     */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = TokyoTime.now();
    }

    /**
     * エンティティIDを取得します。
     *
     * @return エンティティID
     */
    public UUID getId() {
        return id;
    }

    /**
     * 作成日時を取得します。
     *
     * @return 作成日時
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * 更新日時を取得します。
     *
     * @return 更新日時
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
