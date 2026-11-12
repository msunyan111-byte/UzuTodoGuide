package com.example.uzu.web.error;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ApiExceptionHandler} のHTTPステータス変換を検証する単体テストです。
 */
@DisplayName("ApiExceptionHandler の単体テスト")
class ApiExceptionHandlerTest {

    /** テスト対象のハンドラ。 */
    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    /**
     * 未対応のHTTPメソッドを405へ変換することを検証します。
     */
    @Test
    @DisplayName("HTTPメソッド非対応: 405とMETHOD_NOT_ALLOWEDを返す")
    void handleMethodNotAllowed_returns405() {
        ResponseEntity<ApiErrorResponse> response = handler.handleMethodNotAllowed(
                new HttpRequestMethodNotSupportedException("PATCH"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode())
                .isEqualTo(ApiErrorCode.METHOD_NOT_ALLOWED.name());
    }

    /**
     * 未対応のメディアタイプを415へ変換することを検証します。
     */
    @Test
    @DisplayName("メディアタイプ非対応: 415とUNSUPPORTED_MEDIA_TYPEを返す")
    void handleMediaTypeNotSupported_returns415() {
        ResponseEntity<ApiErrorResponse> response = handler.handleMediaTypeNotSupported(
                new HttpMediaTypeNotSupportedException("text/plain"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode())
                .isEqualTo(ApiErrorCode.UNSUPPORTED_MEDIA_TYPE.name());
    }

    /**
     * 想定外の例外を500へ変換することを検証します。
     */
    @Test
    @DisplayName("想定外の例外: 500とINTERNAL_ERRORを返す")
    void handleUnexpected_returns500() {
        ResponseEntity<ApiErrorResponse> response = handler.handleUnexpected(
                new IllegalStateException("boom"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getError().getCode())
                .isEqualTo(ApiErrorCode.INTERNAL_ERROR.name());
    }
}
