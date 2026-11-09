package com.example.uzu.web;

import com.example.uzu.service.BackupService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * バックアップと復元のAPIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1")
public class BackupController {

    /**
     * バックアップサービス
     */
    private final BackupService backupService;

    /**
     * バックアップコントローラを生成します。
     *
     * @param backupService バックアップサービス
     */
    public BackupController(final BackupService backupService) {
        this.backupService = backupService;
    }

    /**
     * 全データをエクスポートします。
     *
     * @return エクスポートしたデータ
     */
    @GetMapping("/backup")
    public Map<String, Object> exportBackup() {
        return backupService.exportAll();
    }

    /**
     * バックアップデータを復元します。
     *
     * @param payload 復元するデータ
     * @return 復元後の全データ
     */
    @PostMapping("/restore")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> restoreBackup(@RequestBody final Map<String, Object> payload) {
        return backupService.restore(payload);
    }
}
