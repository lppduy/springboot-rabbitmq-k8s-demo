package com.demo.order.shared.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Utility class for creating standardized API responses
 * Provides static methods to build ResponseEntity with BaseResponse wrapper
 */
public class ResponseUtils {

    /**
     * Returns a 200 OK response with default message "Success"
     */
    public static <T> ResponseEntity<BaseResponse<T>> ok(T data) {
        return ResponseEntity.ok(new BaseResponse<>(200, "Success", data));
    }

    /**
     * Returns a 200 OK response with a custom message
     */
    public static <T> ResponseEntity<BaseResponse<T>> ok(String message, T data) {
        return ResponseEntity.ok(new BaseResponse<>(200, message, data));
    }

    /**
     * Returns a custom 2xx response with status, message, and data
     */
    public static <T> ResponseEntity<BaseResponse<T>> status(int status, String message, T data) {
        return ResponseEntity.status(status).body(new BaseResponse<>(status, message, data));
    }

    /**
     * Returns a 201 Created response with default message "Created"
     */
    public static <T> ResponseEntity<BaseResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(201, "Created", data));
    }

    /**
     * Returns a 201 Created response with custom message
     */
    public static <T> ResponseEntity<BaseResponse<T>> created(String message, T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new BaseResponse<>(201, message, data));
    }

    /**
     * Returns a 204 No Content response
     */
    public static ResponseEntity<BaseResponse<Void>> noContent() {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new BaseResponse<>(204, "No Content", null));
    }

    /**
     * Returns an error response with HttpStatus and message
     * Data is set to null
     */
    public static <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, null));
    }

    /**
     * Returns an error response with custom status code, message, and data
     */
    public static <T> ResponseEntity<BaseResponse<T>> error(int status, String message, T data) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status, message, data));
    }

    /**
     * Returns an error response with HttpStatus, message, and data
     */
    public static <T> ResponseEntity<BaseResponse<T>> error(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status)
                .body(new BaseResponse<>(status.value(), message, data));
    }
}
