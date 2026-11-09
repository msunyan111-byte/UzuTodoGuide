package com.example.uzu;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * WARデプロイ時のアプリケーション初期化を行うクラスです。
 */
public class ServletInitializer extends SpringBootServletInitializer {

    /**
     * アプリケーションの設定を構築します。
     *
     * @param application Springアプリケーションビルダー
     * @return 設定済みのSpringアプリケーションビルダー
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(UzuTodoApplication.class);
    }
}
