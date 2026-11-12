package com.example.uzu.web.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * APIで発生した例外を共通のエラーレスポンスへ変換するクラスです。
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * ロガー
     */
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /**
     * サービス層の入力検証エラーをHTTP 400へ変換します。
     *
     * @param ex 入力検証エラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(ApiValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(final ApiValidationException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ApiErrorCode.VALIDATION_ERROR.name(), ex.getMessage(), ex.getDetails()));
    }

    /**
     * Bean Validationの入力エラーをHTTP 400へ変換します。
     *
     * @param ex Bean Validationの入力エラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleBeanValidation(final MethodArgumentNotValidException ex) {
        final List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::toDetail)
                .collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ApiErrorCode.VALIDATION_ERROR.name(), "入力内容を確認してください。", details));
    }

    /**
     * リクエストボディの読み込みエラーをHTTP 400へ変換します。
     *
     * @param ex リクエストボディの読み込みエラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleUnreadable(final HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ApiErrorCode.VALIDATION_ERROR.name(), "入力内容を確認してください。",
                        List.of(new ErrorDetail("body", "JSON形式または値が不正です。"))));
    }

    /**
     * パスパラメータやクエリパラメータの型変換エラーをHTTP 400へ変換します。
     *
     * @param ex 型変換エラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(final MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(ApiErrorCode.VALIDATION_ERROR.name(), "入力内容を確認してください。",
                        List.of(new ErrorDetail(ex.getName(), "値が不正です。"))));
    }


    /**
     * 未対応のHTTPメソッドをHTTP 405へ変換します。
     *
     * @param ex 未対応のHTTPメソッド例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotAllowed(
            final HttpRequestMethodNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiErrorResponse.of(ApiErrorCode.METHOD_NOT_ALLOWED.name(),
                        "このHTTPメソッドは利用できません。", List.of()));
    }

    /**
     * 未対応のメディアタイプをHTTP 415へ変換します。
     *
     * @param ex 未対応のメディアタイプ例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMediaTypeNotSupported(
            final HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiErrorResponse.of(ApiErrorCode.UNSUPPORTED_MEDIA_TYPE.name(),
                        "このメディアタイプは利用できません。", List.of()));
    }

    /**
     * リソース未検出エラーをHTTP 404へ変換します。
     *
     * @param ex リソース未検出エラー
     * @return エラーレスポンス
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(final NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.of(ApiErrorCode.NOT_FOUND.name(), ex.getMessage(), List.of()));
    }

    /**
     * 想定外の例外をHTTP 500へ変換します。
     *
     * @param ex 想定外の例外
     * @return エラーレスポンス
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(final Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.of(ApiErrorCode.INTERNAL_ERROR.name(), "サーバーエラーが発生しました。", List.of()));
    }

    /**
     * Bean Validationのフィールドエラーをエラー詳細へ変換します。
     *
     * @param error フィールドエラー
     * @return エラー詳細
     */
    private ErrorDetail toDetail(final FieldError error) {
        return new ErrorDetail(error.getField(), error.getDefaultMessage());
    }
}
