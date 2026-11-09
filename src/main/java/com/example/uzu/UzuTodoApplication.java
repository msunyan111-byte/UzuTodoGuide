package com.example.uzu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * UZU制作TODO管理アプリケーションの起動クラスです。
 */
@SpringBootApplication
public class UzuTodoApplication {

    /**
     * アプリケーションを起動します。
     *
     * @param args 起動引数
     */
    public static void main(final String[] args) {
        SpringApplication.run(UzuTodoApplication.class, args);
    }
}
