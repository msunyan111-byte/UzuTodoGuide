package com.example.uzu.web;

import com.example.uzu.service.PlaytestService;
import com.example.uzu.web.dto.PlaytestRecordCreateRequest;
import com.example.uzu.web.dto.PlaytestRecordResponse;
import com.example.uzu.web.dto.PlaytestRecordUpdateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * テストプレイ記録APIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1/projects/{projectId}/playtests")
public class PlaytestController {

    /**
     * テストプレイサービス
     */
    private final PlaytestService playtestService;

    /**
     * テストプレイコントローラを生成します。
     *
     * @param playtestService テストプレイサービス
     */
    public PlaytestController(final PlaytestService playtestService) {
        this.playtestService = playtestService;
    }

    /**
     * 指定された作品のテストプレイ記録を取得します。
     *
     * @param projectId 作品ID
     * @return テストプレイ記録
     */
    @GetMapping
    public List<PlaytestRecordResponse> list(@PathVariable final UUID projectId) {
        return playtestService.findAll(projectId);
    }

    /**
     * テストプレイ記録を作成します。
     *
     * @param projectId 作品ID
     * @param request テストプレイ記録作成リクエスト
     * @return 作成したテストプレイ記録
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaytestRecordResponse add(@PathVariable final UUID projectId,
                                      @Valid @RequestBody final PlaytestRecordCreateRequest request) {
        return playtestService.create(projectId, request);
    }

    /**
     * テストプレイ記録を更新します。
     *
     * @param projectId 作品ID
     * @param playtestId テストプレイ記録ID
     * @param request テストプレイ記録更新リクエスト
     * @return 更新後のテストプレイ記録
     */
    @PatchMapping("/{playtestId}")
    public PlaytestRecordResponse update(@PathVariable final UUID projectId,
                                         @PathVariable final UUID playtestId,
                                         @Valid @RequestBody final PlaytestRecordUpdateRequest request) {
        return playtestService.update(projectId, playtestId, request);
    }

    /**
     * テストプレイ記録を削除します。
     *
     * @param projectId 作品ID
     * @param playtestId テストプレイ記録ID
     */
    @DeleteMapping("/{playtestId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable final UUID projectId, @PathVariable final UUID playtestId) {
        playtestService.delete(projectId, playtestId);
    }
}
