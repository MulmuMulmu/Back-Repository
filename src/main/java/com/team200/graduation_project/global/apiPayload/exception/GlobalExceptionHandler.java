package com.team200.graduation_project.global.apiPayload.exception;

import com.team200.graduation_project.global.apiPayload.code.GeneralErrorCode;
import com.team200.graduation_project.global.apiPayload.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<ApiResponse<String>> handleCustomException(GeneralException e) {
        GeneralErrorCode status = e.getStatus();
        return ResponseEntity
                .status(status.getStatus())
                .body(ApiResponse.onFailure(status.getCode(), status.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> handleAllException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.onFailure("COMMON500", "서버 내부 오류가 발생했습니다: " + e.getMessage()));
    }
}
