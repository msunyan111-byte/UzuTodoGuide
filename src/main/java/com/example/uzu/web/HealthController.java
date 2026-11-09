package com.example.uzu.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ヘルスチェックAPIを提供するコントローラです。
 */
@RestController
@RequestMapping("/api/v1")
public class HealthController {

    /**
     * アプリケーションの稼働状態を返します。
     *
     * @return 稼働状態
     */
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
